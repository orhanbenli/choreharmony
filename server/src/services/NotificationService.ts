import { NotificationResponse } from "../dtos/NotificationResponse";
import { prisma } from "../lib/prisma";

class NotificationService {
  public static async deleteNotification(
    notificationId: number,
    userId: number
  ): Promise<NotificationResponse[]> {
    await prisma.notification.deleteMany({
      where: {
        id: notificationId,
        destination_user_id: userId,
      },
    });

    return await this.getNotificationsByUser(userId);
  }

  public static async getNotificationsByUser(
    userId: number
  ): Promise<NotificationResponse[]> {
    const notifications = await prisma.notification.findMany({
      where: { destination_user_id: userId },
      include: { destination_user: true },
      orderBy: { create_date: "desc" },
    });

    return notifications;
  }
}

export default NotificationService;
