import { HouseholdMembership, User } from "@prisma/client";

export type HouseholdResponse = {
  id: number;
  join_code: string;
  name: string;
  owner_id: number;
  owner: User;
  members: HouseholdMembership[];
};
