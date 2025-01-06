import express from "express";
import { authenticateToken } from "../middlewares";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import ReviewService from "../services/ReviewService";
import { CommentResponse } from "../dtos/CommentResponse";

const router = express.Router();

router.post<CreateReviewRequest, any>(
  "",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      await ReviewService.createReview(
        request.body.user.id,
        request.body.reviewee_user_id,
        request.body.comment_id,
        request.body.like
      );
      response.sendStatus(OK_CODE);
    } catch (error) {
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.get<any, CommentResponse[]>(
  "/comments",
  authenticateToken,
  async (request, response) => {
    response.status(OK_CODE);
    response.json(await ReviewService.getComments());
  }
);
export default router;
