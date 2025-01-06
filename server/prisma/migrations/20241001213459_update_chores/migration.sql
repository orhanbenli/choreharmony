/*
  Warnings:

  - You are about to drop the column `household_membership_id` on the `Chore` table. All the data in the column will be lost.
  - Added the required column `household_id` to the `Chore` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "Chore" DROP CONSTRAINT "Chore_household_membership_id_fkey";

-- AlterTable
ALTER TABLE "Chore" DROP COLUMN "household_membership_id",
ADD COLUMN     "assigned_user_id" INTEGER,
ADD COLUMN     "household_id" INTEGER NOT NULL;

-- AddForeignKey
ALTER TABLE "Chore" ADD CONSTRAINT "Chore_assigned_user_id_fkey" FOREIGN KEY ("assigned_user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Chore" ADD CONSTRAINT "Chore_household_id_fkey" FOREIGN KEY ("household_id") REFERENCES "Household"("id") ON DELETE CASCADE ON UPDATE CASCADE;
