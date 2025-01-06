-- DropForeignKey
ALTER TABLE "Notification" DROP CONSTRAINT "Notification_id_fkey";

-- AddForeignKey
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_destination_user_id_fkey" FOREIGN KEY ("destination_user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
