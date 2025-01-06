import express from "express";
import { authenticateToken } from "../middlewares";
import { HouseholdChatResponse } from "../dtos/HouseholdChatResponse";
import {
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import HouseholdChatService from "../services/HouseholdChatService";
import { SendHouseholdChatRequest } from "../dtos/SendHouseholdChatRequest";
import { DeleteHouseholdChatRequest } from "../dtos/DeleteHouseholdChatRequest";
import { HouseholdChatWithUserInfo } from "../dtos/HouseholdChatWithUserInfo";

const router = express.Router();

router.get<any, HouseholdChatWithUserInfo[]>(
  "",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const chats = await HouseholdChatService.getChatsForUsersHousehold(
      request.body.user.id
    );

    response.status(OK_CODE);
    response.json(chats.chats);
  }
);

router.post<SendHouseholdChatRequest, HouseholdChatWithUserInfo[]>(
  "",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;

    if (!body.text) return response.sendStatus(INVALID_REQUEST_CODE);

    const message: string = request.body.text.trim();

    if (message.length === 0) {
      return response.sendStatus(INVALID_REQUEST_CODE);
    }

    const userId = body.user.id;

    try {
      const { householdChats, code } =
        await HouseholdChatService.sendHouseholdChat(userId, message);

      if (code === CREATED_CODE && householdChats) {
        response.status(CREATED_CODE);
        response.json(householdChats.chats);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.delete<DeleteHouseholdChatRequest, HouseholdChatWithUserInfo[]>(
  "",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;

    if (!body.household_chat_id)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      const { householdChats, code } =
        await HouseholdChatService.deleteHouseholdChat(
          body.user.id,
          body.household_chat_id
        );

      if (code === OK_CODE && householdChats) {
        response.status(OK_CODE);
        response.json(householdChats.chats);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

export default router;
