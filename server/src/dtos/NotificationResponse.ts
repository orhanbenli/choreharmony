import { User } from "@prisma/client";

export type NotificationResponse = {
  id: number;
  destination_user_id: number;
  destination_user: User;
  notification_type: string;
  navigator_id: number | null;
  content: string;
  create_date: Date;
};
