import { User, UserEmailVerification } from "@prisma/client";
import jwt from "jsonwebtoken";
import { prisma } from "../lib/prisma";
import PasswordHashingService from "./PasswordHashingService";
import {
  CONFLICT_CODE,
  CREATED_CODE,
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import EmailService from "./EmailService";
import { passwordStrength } from "check-password-strength";

class AuthenticationService {
  public static async loginUser(
    email: string,
    password: string
  ): Promise<string> {
    const foundUser: User = await prisma.user.findUniqueOrThrow({
      where: {
        email: email,
      },
    });

    if (!PasswordHashingService.compareHash(password, foundUser.password)) {
      throw new Error("Incorrect Password");
    }

    return jwt.sign(foundUser, process.env.ACCESS_TOKEN_SECRET as string);
  }

  public static async sendEmailVerification(email: string): Promise<number> {
    const doesUserAccountExist: number = await prisma.user.count({
      where: {
        email: email,
      },
    });

    if (doesUserAccountExist > 0) return CONFLICT_CODE;

    const verificationCode = Math.floor(100000 + Math.random() * 900000);

    await EmailService.sendVerificationEmail(email, verificationCode);

    await prisma.userEmailVerification.deleteMany({
      where: { email: email },
    });

    await prisma.userEmailVerification.create({
      data: { email: email, code: verificationCode },
    });

    return OK_CODE;
  }

  public static async register(
    registrationRequest: RegistrationRequest
  ): Promise<RegistrationResponse> {
    const response: RegistrationResponse = {
      code: INTERNAL_ERROR_CODE,
    };

    // Check password strength
    const strength = passwordStrength(registrationRequest.password);

    if (strength.contains.length !== 4 || strength.length < 8) {
      response.code = INVALID_REQUEST_CODE;
      return response;
    }

    // Check that user doesnt already exist
    const doesUserAccountExist: number = await prisma.user.count({
      where: {
        email: registrationRequest.email,
      },
    });

    if (doesUserAccountExist > 0) {
      response.code = CONFLICT_CODE;
      return response;
    }

    // Check email verification code is valid
    const userVerification: UserEmailVerification | null =
      await prisma.userEmailVerification.findFirst({
        where: { email: registrationRequest.email },
      });

    if (!userVerification) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    if (userVerification.code !== registrationRequest.code) {
      response.code = INVALID_REQUEST_CODE;
      return response;
    }

    await prisma.userEmailVerification.delete({
      where: { email: registrationRequest.email },
    });

    // Create user
    const createdUser: User = await prisma.user.create({
      data: {
        email: registrationRequest.email,
        first_name: registrationRequest.first_name,
        last_name: registrationRequest.last_name,
        password: PasswordHashingService.hashPassword(
          registrationRequest.password
        ),
      },
    });

    // Sign and return jwt
    const token: string = jwt.sign(
      createdUser,
      process.env.ACCESS_TOKEN_SECRET as string
    );

    response.code = CREATED_CODE;
    response.token = token;

    return response;
  }
}

export default AuthenticationService;
