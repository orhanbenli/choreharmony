import express from "express";
import { authenticateToken } from "../middlewares";
import { OK_CODE } from "../lib/StatusCodes";
import UserService from "../services/UserService";
import NotificationService from "../services/NotificationService";
import { CreateNotificationRequest } from "../dtos/CreateNotificationRequest";
import { NotificationResponse } from "../dtos/NotificationResponse";

const router = express.Router();

router.delete<NotificationIdRequest, NotificationResponse[]>(
  "/:notificationId",
  authenticateToken,
  async (request, response) => {
    const notifications = await NotificationService.deleteNotification(
      Number(request.params.notificationId),
      request.body.user.id
    );

    response.status(OK_CODE).json(notifications);
  }
);

router.get<any, NotificationResponse[]>(
  "",
  authenticateToken,
  async (request, response) => {
    const notifications = await NotificationService.getNotificationsByUser(
      request.body.user.id
    );

    response.status(OK_CODE).json(notifications);
  }
);

export default router;
