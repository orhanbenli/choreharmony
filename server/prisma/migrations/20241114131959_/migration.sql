-- AddForeignKey
ALTER TABLE "TradeRequests" ADD CONSTRAINT "TradeRequests_chore_id_fkey" FOREIGN KEY ("chore_id") REFERENCES "Chore"("id") ON DELETE CASCADE ON UPDATE CASCADE;
