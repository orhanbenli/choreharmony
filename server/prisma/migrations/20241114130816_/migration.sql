/*
  Warnings:

  - You are about to drop the column `destination_membership_id` on the `TradeRequests` table. All the data in the column will be lost.
  - You are about to drop the column `source_membership_id` on the `TradeRequests` table. All the data in the column will be lost.
  - Added the required column `destination_user_id` to the `TradeRequests` table without a default value. This is not possible if the table is not empty.
  - Added the required column `source_user_id` to the `TradeRequests` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "TradeRequests" DROP CONSTRAINT "TradeRequests_destination_membership_id_fkey";

-- DropForeignKey
ALTER TABLE "TradeRequests" DROP CONSTRAINT "TradeRequests_source_membership_id_fkey";

-- AlterTable
ALTER TABLE "TradeRequests" DROP COLUMN "destination_membership_id",
DROP COLUMN "source_membership_id",
ADD COLUMN     "destination_user_id" INTEGER NOT NULL,
ADD COLUMN     "source_user_id" INTEGER NOT NULL;

-- AddForeignKey
ALTER TABLE "TradeRequests" ADD CONSTRAINT "TradeRequests_source_user_id_fkey" FOREIGN KEY ("source_user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TradeRequests" ADD CONSTRAINT "TradeRequests_destination_user_id_fkey" FOREIGN KEY ("destination_user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
