import { HouseholdMembership, User } from "@prisma/client";
import { HouseholdResponse } from "../dtos/HouseholdResponse";
import { prisma } from "../lib/prisma";
import {
  CONFLICT_CODE,
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
  UNAUTHORIZED_CODE,
} from "../lib/StatusCodes";
import { customAlphabet } from "nanoid";
import { ManageHouseholdJoinResponse } from "../dtos/ManageHouseholdJoinResponse";

class HouseholdService {
  private static nanoid = customAlphabet(
    "1234567890abcdefghijklmnopqrxtuvwxyz",
    4
  );

  public static async createHousehold(
    name: string,
    user: User
  ): Promise<{ household?: HouseholdResponse; code: number }> {
    let response: { household?: HouseholdResponse; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    const userHouseholdOwnershipCount = await prisma.household.count({
      where: {
        owner_id: user.id,
      },
    });

    if (userHouseholdOwnershipCount >= 1) {
      response.code = CONFLICT_CODE;
      return response;
    }

    const userHouseholdMembershipCount = await prisma.householdMembership.count(
      {
        where: {
          user_id: user.id,
          pending_flag: false,
        },
      }
    );

    if (userHouseholdMembershipCount >= 1) {
      response.code = CONFLICT_CODE;
      return response;
    }

    await prisma.householdMembership.deleteMany({
      where: { user_id: user.id, pending_flag: true },
    });

    const createdHousehold = await prisma.household.create({
      data: {
        name: name,
        join_code: `${this.nanoid()}-${this.nanoid()}`,
        owner_id: user.id,
      },
    });

    response.code = CREATED_CODE;
    response.household = {
      owner: user,
      members: [],
      ...createdHousehold,
    };

    return response;
  }

  public static async getUserHousehold(
    userId: number
  ): Promise<HouseholdResponse | undefined> {
    let household = await prisma.household.findFirst({
      where: {
        owner_id: userId,
      },
      include: {
        owner: true,
        members: {
          include: {
            user: true,
          },
        },
      },
    });

    if (household) {
      household.members = household.members.filter((x) => !x.pending_flag);
      return household;
    }

    let householdMembership = await prisma.householdMembership.findFirst({
      where: { user_id: userId, pending_flag: false },
    });

    if (!householdMembership) return undefined;

    const populatedHousehold = await prisma.household.findFirstOrThrow({
      where: { id: householdMembership.household_id },
      include: {
        owner: true,
        members: {
          include: { user: true },
        },
      },
    });

    populatedHousehold.members = populatedHousehold.members.filter(
      (x) => !x.pending_flag
    );

    return populatedHousehold;
  }

  public static async sendJoinRequest(
    userId: number,
    joinCode: string
  ): Promise<{ householdJoinResponse?: HouseholdJoinResponse; code: number }> {
    let response: {
      householdJoinResponse?: HouseholdJoinResponse;
      code: number;
    } = {
      code: INTERNAL_ERROR_CODE,
    };

    const household = await prisma.household.findFirst({
      where: {
        join_code: {
          equals: joinCode,
          mode: "insensitive",
        },
      },
    });

    if (!household) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    if (household.owner_id === userId) {
      response.code = CONFLICT_CODE;
      return response;
    }

    const membershipRequests = await prisma.householdMembership.count({
      where: {
        household_id: household.id,
        user_id: userId,
      },
    });

    if (membershipRequests >= 1) {
      response.code = CONFLICT_CODE;
      return response;
    }

    await prisma.householdMembership.create({
      data: {
        user_id: userId,
        household_id: household.id,
        pending_flag: true,
      },
    });

    response.householdJoinResponse = { name: household.name };
    response.code = CREATED_CODE;

    return response;
  }

  public static async manageHouseholdJoinRequest(
    senderUserId: number,
    joinRequestId: number,
    approvalStatus: boolean
  ): Promise<{
    householdJoinResponse?: ManageHouseholdJoinResponse;
    code: number;
  }> {
    const response: {
      householdJoinResponse?: ManageHouseholdJoinResponse;
      code: number;
    } = {
      code: INTERNAL_ERROR_CODE,
    };

    const membershipRequest = await prisma.householdMembership.findFirst({
      where: { id: joinRequestId },
    });

    if (!membershipRequest) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    const household = await prisma.household.findFirstOrThrow({
      where: { id: membershipRequest.household_id },
      include: {
        owner: true,
        members: true,
      },
    });

    if (membershipRequest.user_id === senderUserId) {
      await prisma.householdMembership.delete({
        where: { id: joinRequestId },
      });

      const householdMembers = await prisma.householdMembership.findMany({
        where: { household_id: household.id },
        include: { user: true },
      });

      response.householdJoinResponse = {
        members: householdMembers.map((x) => x.user),
      };
      response.code = CREATED_CODE;
      return response;
    }

    if (household.owner_id !== senderUserId) {
      response.code = UNAUTHORIZED_CODE;
      return response;
    }

    if (approvalStatus) {
      await prisma.householdMembership.update({
        where: { id: joinRequestId },
        data: { pending_flag: false },
      });
    } else {
      await prisma.householdMembership.delete({
        where: { id: joinRequestId },
      });
    }

    const householdMembers = await prisma.householdMembership.findMany({
      where: { household_id: household.id },
      include: { user: true },
    });

    response.householdJoinResponse = {
      members: householdMembers.map((x) => x.user),
    };

    response.code = CREATED_CODE;
    return response;
  }

  public static async leaveHousehold(userId: number) {
    await prisma.householdMembership.deleteMany({
      where: { user_id: userId },
    });

    await prisma.chore.updateMany({
      where: { assigned_user_id: userId },
      data: {
        assigned_user_id: null,
      },
    });

    await prisma.tradeRequests.deleteMany({
      where: {
        OR: [{ source_user_id: userId }, { destination_user_id: userId }],
      },
    });
  }

  public static async deleteHousehold(userId: number) {
    await prisma.household.deleteMany({
      where: { owner_id: userId },
    });
  }

  public static async getUserSentHouseholdJoinRequests(
    userId: number
  ): Promise<HouseholdMembership[]> {
    return await prisma.householdMembership.findMany({
      where: { user_id: userId },
      include: { user: true, household: true },
    });
  }

  public static async getPendingHouseholdJoinRequests(
    userId: number
  ): Promise<HouseholdMembership[] | undefined> {
    const household = await this.getUserHousehold(userId);

    if (!household) {
      return undefined;
    }

    return await prisma.householdMembership.findMany({
      where: { household_id: household.id, pending_flag: true },
      include: { user: true, household: true },
    });
  }
}

export default HouseholdService;
