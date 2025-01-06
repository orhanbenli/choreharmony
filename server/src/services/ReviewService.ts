import { CommentResponse } from "../dtos/CommentResponse";
import { prisma } from "../lib/prisma";

export default class ReviewService {
  constructor() {}

  public static async createReview(
    reviewer_user_id: number,
    reviewee_user_id: number,
    comment_id: number,
    like: boolean
  ) {
    await prisma.userReviews.deleteMany({
      where: {
        reviewer_user_id: reviewer_user_id,
        reviewee_user_id: reviewee_user_id,
      },
    });

    await prisma.userReviews.create({
      data: {
        reviewer_user_id: reviewer_user_id,
        reviewee_user_id: reviewee_user_id,
        review_comment_id: comment_id,
        like: like,
      },
    });
  }

  public static async getComments(): Promise<CommentResponse[]> {
    return await prisma.reviewComments.findMany();
  }
}
