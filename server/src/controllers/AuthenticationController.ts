import express from "express";
import AuthenticationService from "../services/AuthenticationService";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  OK_CODE,
  UNAUTHORIZED_CODE,
} from "../lib/StatusCodes";

const router = express.Router();

router.post<LoginRequest, TokenResponse>(
  "/login",
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;
    if (!body.email || !body.password)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const email = body.email;
    const password = body.password;

    try {
      const token = await AuthenticationService.loginUser(email, password);

      response.status(OK_CODE);
      response.json({ data: token });
    } catch (error) {
      console.log(error);
      response.sendStatus(UNAUTHORIZED_CODE);
    }
  }
);

router.post<LoginRequest, TokenResponse>(
  "/send-email-verification",
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const body = request.body;
    if (!body.email) return response.sendStatus(INVALID_REQUEST_CODE);

    const email = body.email;

    try {
      const emailSuccessCode =
        await AuthenticationService.sendEmailVerification(email);

      response.sendStatus(emailSuccessCode);
    } catch (error) {
      console.log(error);
      response.sendStatus(INTERNAL_ERROR_CODE);
    }
  }
);

router.post<RegistrationRequest, TokenResponse>(
  "/register",
  async (request, response) => {
    if (!request || !request.body)
      return response.sendStatus(INVALID_REQUEST_CODE);

    const registrationRequest: RegistrationRequest =
      request.body as RegistrationRequest;

    if (
      !registrationRequest.email ||
      !registrationRequest.password ||
      !registrationRequest.first_name ||
      !registrationRequest.last_name ||
      !registrationRequest.code
    )
      return response.sendStatus(INVALID_REQUEST_CODE);

    registrationRequest.code = Number(registrationRequest.code);

    try {
      const { code, token } = await AuthenticationService.register(
        registrationRequest
      );

      if (token) {
        response.status(code);
        response.json({ data: token });
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
