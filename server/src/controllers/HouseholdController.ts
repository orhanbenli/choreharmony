import express from "express";
import { authenticateToken } from "../middlewares";
import { HouseholdResponse } from "../dtos/HouseholdResponse";
import {
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import HouseholdService from "../services/HouseholdService";
import { ManageHouseholdJoinResponse } from "../dtos/ManageHouseholdJoinResponse";
import { HouseholdMembership } from ".prisma/client";

const router = express.Router();

router.post<CreateHouseholdRequest, HouseholdResponse>(
  "/",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;

    if (!body.name || body.name.length <= 0)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const name = body.name;

    try {
      const { household, code } = await HouseholdService.createHousehold(
        name,
        request.body.user
      );

      if (code === CREATED_CODE) {
        response.status(code);
        response.json(household);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<any, HouseholdResponse>(
  "/my-household",
  authenticateToken,
  async (request, response) => {
    const myHousehold = await HouseholdService.getUserHousehold(
      request.body.user.id
    );

    if (!myHousehold) return response.sendStatus(NOT_FOUND_CODE);

    response.status(OK_CODE);
    response.json(myHousehold);
  }
);

router.post<HouseholdJoinRequest, HouseholdJoinResponse>(
  "/send-join-request",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;

    if (!body.joinCode) return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      const { householdJoinResponse, code } =
        await HouseholdService.sendJoinRequest(body.user.id, body.joinCode);

      if (code === CREATED_CODE) {
        response.status(CREATED_CODE);
        response.json(householdJoinResponse);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<ManageHouseholdJoinRequest, ManageHouseholdJoinResponse>(
  "/manage-join-request",
  authenticateToken,
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;

    if (!body.id || body.approve === undefined)
      return response.sendStatus(INVALID_REQUEST_CODE);

    try {
      const { householdJoinResponse, code } =
        await HouseholdService.manageHouseholdJoinRequest(
          body.user.id,
          body.id,
          body.approve
        );

      if (code === CREATED_CODE) {
        response.status(CREATED_CODE);
        response.json(householdJoinResponse);
      } else {
        response.sendStatus(code);
      }
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.delete<any, any>(
  "/leave",
  authenticateToken,
  async (request, response) => {
    await HouseholdService.leaveHousehold(request.body.user.id);

    response.sendStatus(OK_CODE);
  }
);

router.delete<LoginRequest, TokenResponse>(
  "/",
  authenticateToken,
  async (request, response) => {
    await HouseholdService.deleteHousehold(request.body.user.id);

    response.sendStatus(OK_CODE);
  }
);

router.get<any, HouseholdMembership[]>(
  "/sent-requests",
  authenticateToken,
  async (request, response) => {
    response.status(OK_CODE);
    response.json(
      await HouseholdService.getUserSentHouseholdJoinRequests(
        request.body.user.id
      )
    );
  }
);

router.get<any, HouseholdMembership[]>(
  "/pending-household-join-requests",
  authenticateToken,
  async (request, response) => {
    const householdMembers =
      await HouseholdService.getPendingHouseholdJoinRequests(
        request.body.user.id
      );

    if (!householdMembers) {
      response.sendStatus(NOT_FOUND_CODE);
      return;
    }

    response.status(OK_CODE);
    response.json(householdMembers);
  }
);

export default router;
