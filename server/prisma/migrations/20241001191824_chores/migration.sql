-- CreateTable
CREATE TABLE "Chore" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "completion_date" TIMESTAMP(3),
    "recurrence_in_days" INTEGER,
    "household_membership_id" INTEGER NOT NULL,

    CONSTRAINT "Chore_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "Chore" ADD CONSTRAINT "Chore_household_membership_id_fkey" FOREIGN KEY ("household_membership_id") REFERENCES "HouseholdMembership"("id") ON DELETE CASCADE ON UPDATE CASCADE;
