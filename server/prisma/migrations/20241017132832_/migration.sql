-- CreateTable
CREATE TABLE "HouseholdChat" (
    "id" SERIAL NOT NULL,
    "user_id" INTEGER NOT NULL,
    "household_id" INTEGER NOT NULL,
    "message" TEXT NOT NULL,
    "create_date" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "HouseholdChat_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "HouseholdChat" ADD CONSTRAINT "HouseholdChat_household_id_fkey" FOREIGN KEY ("household_id") REFERENCES "Household"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HouseholdChat" ADD CONSTRAINT "HouseholdChat_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
