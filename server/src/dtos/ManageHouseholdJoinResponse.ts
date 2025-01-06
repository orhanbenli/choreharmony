import { User } from "@prisma/client";

export type ManageHouseholdJoinResponse = {
  members: User[];
};
