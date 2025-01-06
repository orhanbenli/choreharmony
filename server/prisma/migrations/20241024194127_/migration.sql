-- CreateEnum
CREATE TYPE "NotificationType" AS ENUM ('CHORE', 'CHAT');

-- CreateTable
CREATE TABLE "Notification" (
    "id" SERIAL NOT NULL,
    "destination_user_id" INTEGER NOT NULL,
    "notification_type" "NotificationType" NOT NULL,
    "navigator_id" INTEGER,
    "content" TEXT NOT NULL,
    "create_date" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Notification_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "Notification" ADD CONSTRAINT "Notification_id_fkey" FOREIGN KEY ("id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
