import express from "express";
import { authenticateToken } from "../middlewares";
import {
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import { ChoreResponse } from "../dtos/ChoreResponse";
import ChoreService from "../services/ChoreService";
import { User } from "@prisma/client";
import TradeResponse from "../dtos/TradeResponse";
import TradeService from "../services/TradeService";

const router = express.Router();

router.get<any, ChoreResponse[]>(
  "/household-chores",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      const { chores, code } = await ChoreService.getHouseholdChores(
        request.body.user.id
      );

      if (code === OK_CODE) {
        response.status(code);
        response.json(chores);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.get<any, ChoreResponse[]>(
  "/my-chores",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      const { chores, code } = await ChoreService.getUserChores(
        request.body.user.id
      );

      if (code === OK_CODE) {
        response.status(code);
        response.json(chores);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<CreateChoreRequest, ChoreResponse>(
  "/",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;

    if (!body.name || body.name.length <= 0)
      return response.sendStatus(INVALID_REQUEST_CODE);

    if (body.recurrence_in_days && body.recurrence_in_days <= 0)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      const { chore, code } = await ChoreService.createChore(body.user.id, {
        assigned_user_id: body.assigned_user_id,
        name: body.name,
        recurrence_in_days: body.recurrence_in_days,
      });

      if (code == CREATED_CODE) {
        response.status(code);
        response.json(chore);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<ChoreIdRequest, ChoreResponse>(
  "/complete-chore/:choreId",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;

    if (!request.params.choreId || isNaN(Number(request.params.choreId)))
      return response.sendStatus(INVALID_REQUEST_CODE);

    const choreId = Number(request.params.choreId);

    try {
      const { chore, code } = await ChoreService.completeChore(
        body.user.id,
        choreId
      );

      if (code == CREATED_CODE) {
        response.status(code);
        response.json(chore);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.get<ChoreIdRequest, ChoreResponse>(
  "/details/:choreId",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    if (!request.params.choreId || isNaN(Number(request.params.choreId)))
      return response.sendStatus(INVALID_REQUEST_CODE);

    const choreId = Number(request.params.choreId);

    try {
      const chore = await ChoreService.getChore(choreId);

      if (!chore) {
        response.sendStatus(NOT_FOUND_CODE);
      } else {
        response.status(OK_CODE);
        response.json(chore);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<ChoreIdRequest, ChoreResponse>(
  "/knock/:choreId",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    if (!request.params.choreId || isNaN(Number(request.params.choreId)))
      return response.sendStatus(INVALID_REQUEST_CODE);

    const choreId = Number(request.params.choreId);

    try {
      const chore = await ChoreService.knock(choreId);

      if (!chore) {
        response.sendStatus(NOT_FOUND_CODE);
      } else {
        response.status(OK_CODE);
        response.json(chore);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<any, any>(
  "/reassign",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      await ChoreService.reassignChore(
        request.body.chore_id,
        request.body.user_id
      );

      response.sendStatus(OK_CODE);
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.get<any, User[]>(
  "/assignable-members",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      const { users, code } = await ChoreService.getAssignableMembers(
        request.body.user.id
      );

      if (code === OK_CODE) {
        response.status(code);
        response.json(users);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.get<any, TradeResponse[]>(
  "/trades/sent-requests",
  authenticateToken,
  async (req, res) => {
    const userId = req.body.user.id;

    res.status(OK_CODE);
    res.json(await TradeService.getSentTradeRequests(userId));
  }
);

router.get<any, TradeResponse[]>(
  "/trades/pending-requests",
  authenticateToken,
  async (req, res) => {
    const userId = req.body.user.id;

    res.status(OK_CODE);
    res.json(await TradeService.getPendingTradeRequests(userId));
  }
);

router.post<TradeRequest, TradeResponse[]>(
  "/trades/",
  authenticateToken,
  async (req, res) => {
    const destinationUserId = req.body.destination_user_id;
    const householdPower = req.body.household_power;
    const choreId = req.body.chore_id;
    const sourceUserId = req.body.user.id;

    const success = await TradeService.createTradeRequest(sourceUserId, {
      destination_user_id: destinationUserId,
      household_power: householdPower,
      chore_id: choreId,
    });

    if (success) {
      res.sendStatus(CREATED_CODE);
    } else {
      res.sendStatus(NOT_FOUND_CODE);
    }
  }
);

router.delete<ManageTradeRequest, TradeResponse[]>(
  "/trades/sent-requests",
  authenticateToken,
  async (req, res) => {
    const userId = req.body.user.id;
    const tradeId = req.body.trade_id;

    res.status(OK_CODE);
    res.json(await TradeService.deleteSentTradeRequest(userId, tradeId));
  }
);

router.post<ManageTradeRequest, TradeResponse[]>(
  "/trades/pending-requests",
  authenticateToken,
  async (req, res) => {
    const userId = req.body.user.id;
    const tradeId = req.body.trade_id;
    const approval = req.body.approval;

    const tradeRequests = await TradeService.manageTradeRequest(
      userId,
      tradeId,
      approval
    );

    if (tradeRequests === undefined) {
      res.sendStatus(NOT_FOUND_CODE);
    } else {
      res.status(OK_CODE);
      res.json(tradeRequests);
    }
  }
);

export default router;
