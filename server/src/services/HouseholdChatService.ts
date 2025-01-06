import { HouseholdChatResponse } from "../dtos/HouseholdChatResponse";
import { HouseholdChatWithUserInfo } from "../dtos/HouseholdChatWithUserInfo";
import { prisma } from "../lib/prisma";
import {
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
  UNAUTHORIZED_CODE,
} from "../lib/StatusCodes";
import HouseholdService from "./HouseholdService";
import EmailService from "./EmailService";
import { NotificationType } from "../dtos/NotificationType";

export default class HouseholdChatService {
  static async getChatsForUsersHousehold(
    userId: number
  ): Promise<HouseholdChatResponse> {
    const household = await HouseholdService.getUserHousehold(userId);
    let response: HouseholdChatResponse = {
      chats: [],
    };

    if (!household) {
      return response;
    }

    const householdChats: HouseholdChatWithUserInfo[] =
      await prisma.householdChat.findMany({
        where: {
          household_id: household.id,
        },
        include: {
          user: true,
        },
        orderBy: {
          create_date: "asc",
        },
      });

    response.chats = householdChats;

    return response;
  }

  static async sendHouseholdChat(
    userId: number,
    message: string
  ): Promise<{ householdChats?: HouseholdChatResponse; code: number }> {
    const household = await HouseholdService.getUserHousehold(userId);
    let response: { householdChats?: HouseholdChatResponse; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    if (!household) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    await prisma.householdChat.create({
      data: {
        user_id: userId,
        household_id: household.id,
        message,
        create_date: new Date(),
      },
    });

    const recipient_ids = household.members
      .filter((x) => !x.pending_flag)
      .map((x) => x.user_id)
      .concat(household.owner_id)
      .filter((x) => x !== userId);

    await prisma.notification.deleteMany({
      where: {
        destination_user_id: { in: recipient_ids },
        notification_type: NotificationType.CHAT,
      },
    });

    await prisma.notification.createMany({
      data: recipient_ids.map((recipient_id) => {
        return {
          destination_user_id: recipient_id,
          notification_type: NotificationType.CHAT,
          content: "New messages in the household chat!",
          create_date: new Date(),
        };
      }),
    });

    let source = await prisma.user.findFirst({
      where: { id: userId },
    });

    if (source) {
      let recipients = await prisma.user.findMany({
        where: { id: { in: recipient_ids } },
      });

      await EmailService.sendNewChatEmail(source, recipients, message);
    }

    const updatedChats = await prisma.householdChat.findMany({
      where: {
        household_id: household.id,
      },
      include: {
        user: true,
      },
      orderBy: {
        create_date: "asc",
      },
    });

    response.householdChats = {
      chats: updatedChats,
    };
    response.code = CREATED_CODE;

    return response;
  }

  static async deleteHouseholdChat(
    userId: number,
    householdChatId: number
  ): Promise<{ householdChats?: HouseholdChatResponse; code: number }> {
    const household = await HouseholdService.getUserHousehold(userId);
    let response: { householdChats?: HouseholdChatResponse; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    if (!household) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    const chat = await prisma.householdChat.findFirst({
      where: { id: householdChatId },
    });

    if (!chat) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    if (household.owner_id === userId || chat.user_id === userId) {
      await prisma.householdChat.delete({
        where: { id: householdChatId },
      });

      const chats = await prisma.householdChat.findMany({
        where: {
          household_id: household.id,
        },
        include: {
          user: true,
        },
        orderBy: {
          create_date: "asc",
        },
      });

      response.householdChats = { chats: chats };
      response.code = OK_CODE;
      return response;
    } else {
      response.code = UNAUTHORIZED_CODE;
      return response;
    }
  }
}
