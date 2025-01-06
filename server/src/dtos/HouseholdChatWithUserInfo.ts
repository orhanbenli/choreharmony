import { HouseholdChat, User } from "@prisma/client";

export type HouseholdChatWithUserInfo = HouseholdChat & { user: User };
