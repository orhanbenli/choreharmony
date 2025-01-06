import { User } from "@prisma/client";
import { ChoreResponse } from "../dtos/ChoreResponse";
import { prisma } from "../lib/prisma";
import {
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import HouseholdService from "./HouseholdService";
import EmailService from "./EmailService";
import { NotificationType } from "../dtos/NotificationType";
import ms from "ms";

class ChoreService {
  public static async getHouseholdChores(
    userId: number
  ): Promise<{ chores?: ChoreResponse[]; code: number }> {
    const response: { chores?: ChoreResponse[]; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    const household = await HouseholdService.getUserHousehold(userId);

    if (!household) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    const chores = await prisma.chore.findMany({
      where: { household_id: household.id },
      include: { user: true, household: true },
    });

    response.chores = chores.map((x) => {
      return {
        ...x,
        household: x.household,
        assigned_user: x.user,
      };
    });
    response.code = OK_CODE;

    return response;
  }

  public static async getUserChores(
    userId: number
  ): Promise<{ chores?: ChoreResponse[]; code: number }> {
    const response: { chores?: ChoreResponse[]; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    const chores = await prisma.chore.findMany({
      where: { assigned_user_id: userId },
      include: { user: true, household: true },
    });

    response.chores = chores.map((x) => {
      return {
        ...x,
        household: x.household,
        assigned_user: x.user,
      };
    });
    response.code = OK_CODE;

    return response;
  }

  public static async knock(choreId: number): Promise<ChoreResponse | null> {
    const chore = await this.getChore(choreId);

    if (!chore || !chore.assigned_user_id) return null;

    const assignedUser = chore.assigned_user;
    if (!assignedUser) return null;

    const knockDate = new Date();

    await prisma.notification.create({
      data: {
        destination_user_id: chore.assigned_user_id,
        content: `Knock, Knock! ${chore.name}`,
        create_date: knockDate,
        notification_type: NotificationType.CHORE,
        navigator_id: chore.id,
      },
    });

    await prisma.user.update({
      where: { id: chore.assigned_user_id },
      data: { household_power: assignedUser.household_power - 1 },
    });

    await EmailService.sendKnockReminder(chore);

    await prisma.chore.update({
      where: { id: choreId },
      data: {
        last_reminder_date: knockDate,
      },
    });

    chore.last_reminder_date = knockDate;

    return chore;
  }

  public static async completeChore(
    userId: number,
    choreId: number
  ): Promise<{ chore?: ChoreResponse; code: number }> {
    const response: { chore?: ChoreResponse; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };
    const household = await HouseholdService.getUserHousehold(userId);

    if (!household) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    await prisma.chore.update({
      where: { id: choreId },
      data: { completion_date: new Date() },
    });

    const userIds = household.members
      .filter((x) => !x.pending_flag)
      .map((x) => x.user_id)
      .concat(household.owner_id)
      .filter((x) => x !== userId);

    const usersForNotification = await prisma.user.findMany({
      where: { id: { in: userIds } },
    });

    const completingUser = await prisma.user.findFirst({
      where: { id: userId },
    });

    if (completingUser) {
      await prisma.user.update({
        where: { id: userId },
        data: { household_power: completingUser.household_power + 1 },
      });
    }

    const updatedChore = await prisma.chore.findFirstOrThrow({
      where: { id: choreId },
      include: { household: true, user: true },
    });

    await prisma.notification.createMany({
      data: usersForNotification.map((user) => {
        return {
          destination_user_id: user.id,
          create_date: new Date(),
          navigator_id: choreId,
          notification_type: NotificationType.CHORE,
          content: `Completed: ${updatedChore.name}`,
        };
      }),
    });

    await EmailService.sendChoreCompletedEmail(
      usersForNotification,
      updatedChore
    );

    response.code = CREATED_CODE;
    response.chore = {
      ...updatedChore,
      household: household,
      assigned_user: updatedChore.user,
    };

    return response;
  }

  public static async getChore(choreId: number): Promise<ChoreResponse | null> {
    const chore = await prisma.chore.findFirst({
      where: { id: choreId },
      include: { user: true, household: true },
    });

    if (!chore) return null;

    return {
      ...chore,
      household: chore.household,
      assigned_user: chore.user,
    };
  }

  public static async createChore(
    userId: number,
    createChorePayload: CreateChoreRequest
  ): Promise<{ chore?: ChoreResponse; code: number }> {
    const response: { chore?: ChoreResponse; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    if (createChorePayload.name.trim().length <= 0) {
      response.code = INVALID_REQUEST_CODE;
      return response;
    }

    if (
      createChorePayload.recurrence_in_days != undefined &&
      createChorePayload.recurrence_in_days <= 0
    ) {
      response.code = INVALID_REQUEST_CODE;
      return response;
    }

    const household = await HouseholdService.getUserHousehold(userId);

    if (!household) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    let user = undefined;
    if (createChorePayload.assigned_user_id !== undefined) {
      user = await prisma.householdMembership.findFirst({
        where: {
          user_id: createChorePayload.assigned_user_id,
          household_id: household.id,
        },
      });

      if (!user && household.owner_id !== createChorePayload.assigned_user_id) {
        response.code = NOT_FOUND_CODE;
        return response;
      }
    }

    const createdChore = await prisma.chore.create({
      data: {
        household_id: household.id,
        assigned_user_id: createChorePayload.assigned_user_id,
        recurrence_in_days: createChorePayload.recurrence_in_days,
        name: createChorePayload.name,
      },
      include: {
        household: true,
        user: true,
      },
    });

    response.code = CREATED_CODE;
    response.chore = {
      ...createdChore,
      household: createdChore.household,
      assigned_user: createdChore.user,
    };
    return response;
  }

  public static async getAssignableMembers(
    userId: number
  ): Promise<{ users?: User[]; code: number }> {
    const response: { users?: User[]; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    const household = await HouseholdService.getUserHousehold(userId);
    if (!household) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    let assignableMembers: User[] = [household.owner];

    const memberIds = household.members
      .filter((x) => !x.pending_flag)
      .map((x) => x.user_id);

    const members = await prisma.user.findMany({
      where: { id: { in: memberIds } },
    });

    assignableMembers = assignableMembers.concat(members);

    response.users = assignableMembers;
    response.code = OK_CODE;

    return response;
  }

  public static async sendReminders() {
    let chores = await prisma.chore.findMany({
      where: { assigned_user_id: { not: null } },
      include: { user: true },
    });

    const currentDate = Date.now();

    chores = chores
      .filter((x) => x.assigned_user_id) // Assigned to a user
      .filter(
        (x) =>
          !x.last_reminder_date || // Never reminded
          x.last_reminder_date.getTime() + ms("24h") < currentDate // Last reminder was over 24 hours ago
      );

    let recurringChores = chores
      .filter((x) => x.recurrence_in_days != null) // Has a recurrence value
      .filter(
        (x) =>
          !x.completion_date || // Never completed
          x.completion_date.getTime() + ms(`${x.recurrence_in_days}d`) <
            currentDate // Or last completion was over recurrence days ago
      );

    let oneTimeChores = chores.filter(
      (x) => !x.recurrence_in_days && !x.completion_date // No recurrence and never completed
    );

    await prisma.chore.updateMany({
      where: {
        id: {
          in: recurringChores
            .map((x) => x.id)
            .concat(oneTimeChores.map((x) => x.id)),
        },
      },
      data: { last_reminder_date: new Date() },
    });

    let choresForNotification = recurringChores.concat(oneTimeChores);

    await prisma.notification.createMany({
      data: choresForNotification.map((x) => {
        return {
          destination_user_id: x.assigned_user_id || -1,
          content: `Reminder: ${x.name}`,
          create_date: new Date(),
          navigator_id: x.id,
          notification_type: NotificationType.CHORE,
        };
      }),
    });
  }

  public static async reassignChore(choreId: number, userId: number) {
    await prisma.chore.update({
      where: { id: choreId },
      data: { assigned_user_id: userId },
    });
  }
}

export default ChoreService;
