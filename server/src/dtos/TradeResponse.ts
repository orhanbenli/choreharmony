import { Chore, User } from "@prisma/client";

type TradeResponse = {
  id: number;
  source_user_id: number;
  source_user: User;
  destination_user_id: number;
  destination_user: User;
  chore_id: number;
  chore: Chore;
  household_power: number;
};

export default TradeResponse;
