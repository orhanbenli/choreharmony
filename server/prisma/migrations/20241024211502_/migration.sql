/*
  Warnings:

  - Changed the type of `notification_type` on the `Notification` table. No cast exists, the column would be dropped and recreated, which cannot be done if there is data, since the column is required.

*/
-- AlterTable
ALTER TABLE "Notification" DROP COLUMN "notification_type",
ADD COLUMN     "notification_type" TEXT NOT NULL;

-- DropEnum
DROP TYPE "NotificationType";
