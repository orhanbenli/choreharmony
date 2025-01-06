import { Household, User } from "@prisma/client";

export type ChoreResponse = {
  id: number;
  name: string;
  recurrence_in_days: number | null;
  completion_date: Date | null;
  household: Household;
  household_id: number;
  assigned_user: User | null;
  assigned_user_id: number | null;
  last_reminder_date: Date | null;
};
