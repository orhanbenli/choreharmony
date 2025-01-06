import { User } from "@prisma/client";
import { CommentResponse } from "./CommentResponse";

export type ReviewResponse = {
  id: number;
  reviewer_user_id: number;
  reviewee_user_id: number;
  review_comment_id: number;
  like: boolean;

  reviewer: User;
  reviewee: User;
  comment: CommentResponse;
};
