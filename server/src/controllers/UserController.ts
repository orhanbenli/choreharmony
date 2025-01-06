import express from "express";
import { authenticateToken } from "../middlewares";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import UserService from "../services/UserService";

const router = express.Router();

router.delete<any, any>("", authenticateToken, async (request, response) => {
  await UserService.deleteUser(request.body.user.id);
  response.sendStatus(OK_CODE);
});

router.put<any, any>(
  "/email-notifications",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const newValue = request.body.enabled;

    await UserService.manageEmailNotifications(request.body.user.id, newValue);

    response.sendStatus(OK_CODE);
  }
);

router.get<any, any>(
  "/email-notifications",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const notifications = await UserService.getEmailNotificationsEnabled(
      request.body.user.id
    );

    response.status(OK_CODE).json({ data: notifications });
  }
);

router.post("/download-data", authenticateToken, async (req, res) => {
  await UserService.sendDownloadDataEmail(req.body.user.id);

  res.sendStatus(OK_CODE);
});

router.put("/change-password", authenticateToken, async (request, response) => {
  if (!request || !request.body)
    return response.sendStatus(INVALID_REQUEST_CODE);

  const body = request.body;

  if (!body.new_password || !body.old_password)
    return response.sendStatus(INVALID_REQUEST_CODE);

  try {
    const { token, code } = await UserService.changePassword(
      body.user.id,
      body.old_password,
      body.new_password
    );

    if (code === OK_CODE) {
      response.status(OK_CODE);
      response.json({ data: token });
    } else {
      response.sendStatus(code);
    }
  } catch (error) {
    console.log(error);
    response.sendStatus(INTERNAL_ERROR_CODE);
  }
});

router.get<any, any>(
  "/:userId/details",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const user = await UserService.getUserDetails(
      Number(request.params.userId)
    );

    if (!user) {
      response.sendStatus(NOT_FOUND_CODE);
      return;
    }

    response.status(OK_CODE);
    response.json(user);
  }
);

export default router;
