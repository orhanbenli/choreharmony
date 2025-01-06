import { ReviewResponse } from "./ReviewResponse";

type UserDetailResponse = {
  id: number;
  first_name: string;
  last_name: string;
  email: string;
  household_power: number;
  reviews: ReviewResponse[];
};

export default UserDetailResponse;
