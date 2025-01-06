import { NotificationType } from "../dtos/NotificationType";
import TradeResponse from "../dtos/TradeResponse";
import { prisma } from "../lib/prisma";
import EmailService from "./EmailService";
import NotificationService from "./NotificationService";

class TradeService {
  public static async getSentTradeRequests(
    userId: number
  ): Promise<TradeResponse[]> {
    const trades = await prisma.tradeRequests.findMany({
      where: { source_user_id: userId },
      include: { destination_user: true, source_user: true, chore: true },
    });

    const tradeResponses: TradeResponse[] = trades.map((x) => {
      return {
        ...x,
        source_user: x.source_user,
        destination_user: x.destination_user,
        chore: x.chore,
      };
    });

    return tradeResponses;
  }

  public static async getPendingTradeRequests(
    userId: number
  ): Promise<TradeResponse[]> {
    const trades = await prisma.tradeRequests.findMany({
      where: { destination_user_id: userId },
      include: { destination_user: true, source_user: true, chore: true },
    });

    const tradeResponses: TradeResponse[] = trades.map((x) => {
      return {
        ...x,
        source_user: x.source_user,
        destination_user: x.destination_user,
        chore: x.chore,
      };
    });

    return tradeResponses;
  }

  public static async deleteSentTradeRequest(
    userId: number,
    trade_id: number
  ): Promise<TradeResponse[]> {
    await prisma.tradeRequests.deleteMany({
      where: { source_user_id: userId, id: trade_id },
    });

    return await this.getSentTradeRequests(userId);
  }

  public static async manageTradeRequest(
    userId: number,
    trade_id: number,
    approval: boolean
  ): Promise<TradeResponse[] | undefined> {
    const tradeRequest = await prisma.tradeRequests.findFirst({
      where: { id: trade_id, destination_user_id: userId },
      include: { source_user: true, destination_user: true },
    });

    if (!tradeRequest) return;

    await prisma.tradeRequests.deleteMany({
      where: { id: trade_id },
    });

    if (approval === false) return await this.getPendingTradeRequests(userId);

    await prisma.chore.update({
      where: { id: tradeRequest.chore_id },
      data: { assigned_user_id: tradeRequest.destination_user_id },
    });

    await prisma.user.update({
      where: { id: tradeRequest.source_user_id },
      data: {
        household_power:
          tradeRequest.source_user.household_power -
          tradeRequest.household_power,
      },
    });

    await prisma.user.update({
      where: { id: tradeRequest.destination_user_id },
      data: {
        household_power:
          tradeRequest.destination_user.household_power +
          tradeRequest.household_power,
      },
    });

    return await this.getPendingTradeRequests(userId);
  }

  public static async createTradeRequest(
    userId: number,
    tradeRequest: TradeRequest
  ): Promise<boolean> {
    const user = await prisma.user.findFirst({
      where: { id: tradeRequest.destination_user_id },
    });

    if (!user) return false;

    await prisma.tradeRequests.deleteMany({
      where: { chore_id: tradeRequest.chore_id },
    });

    const createdTradeRequest = await prisma.tradeRequests.create({
      data: {
        source_user_id: userId,
        chore_id: tradeRequest.chore_id,
        destination_user_id: tradeRequest.destination_user_id,
        household_power: tradeRequest.household_power,
      },
    });

    if (!createdTradeRequest) return false;

    await prisma.notification.create({
      data: {
        destination_user_id: tradeRequest.destination_user_id,
        content: "New Chore Trade Request!",
        notification_type: NotificationType.TRADE,
        create_date: new Date(),
      },
    });

    await EmailService.sendTradeEmail(user);

    return true;
  }
}

export default TradeService;
