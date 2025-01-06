-- CreateTable
CREATE TABLE "TradeRequests" (
    "id" SERIAL NOT NULL,
    "source_membership_id" INTEGER NOT NULL,
    "destination_membership_id" INTEGER NOT NULL,
    "chore_id" INTEGER NOT NULL,
    "household_power" INTEGER NOT NULL,

    CONSTRAINT "TradeRequests_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "TradeRequests" ADD CONSTRAINT "TradeRequests_source_membership_id_fkey" FOREIGN KEY ("source_membership_id") REFERENCES "HouseholdMembership"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TradeRequests" ADD CONSTRAINT "TradeRequests_destination_membership_id_fkey" FOREIGN KEY ("destination_membership_id") REFERENCES "HouseholdMembership"("id") ON DELETE CASCADE ON UPDATE CASCADE;
