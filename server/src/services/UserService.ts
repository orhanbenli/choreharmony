import { passwordStrength } from "check-password-strength";
import { prisma } from "../lib/prisma";
import jwt from "jsonwebtoken";
import {
  INTERNAL_ERROR_CODE,
  INVALID_REQUEST_CODE,
  NOT_FOUND_CODE,
  OK_CODE,
} from "../lib/StatusCodes";
import EmailService from "./EmailService";
import PasswordHashingService from "./PasswordHashingService";
import { User } from "@prisma/client";
import UserDetailResponse from "../dtos/UserDetailResponse";

export default class UserService {
  public static async deleteUser(userId: number) {
    await prisma.user.deleteMany({
      where: { id: userId },
    });
  }

  public static async getEmailNotificationsEnabled(
    userId: number
  ): Promise<boolean> {
    const value = await prisma.user.findFirst({
      where: { id: userId },
    });

    if (!value) return false;

    return value.email_notifications_enabled;
  }

  public static async manageEmailNotifications(
    userId: number,
    newValue: boolean
  ) {
    await prisma.user.update({
      where: { id: userId },
      data: {
        email_notifications_enabled: newValue,
      },
    });
  }

  public static async sendDownloadDataEmail(userId: number) {
    const user = await prisma.user.findFirst({
      where: { id: userId },
      include: {
        households: true,
        chores: true,
        chats: true,
        notifications: true,
      },
    });

    if (!user) return;

    user.password = "REDACTED";

    await EmailService.sendDownloadDataEmail(user);
  }

  public static async changePassword(
    userId: number,
    oldPassword: string,
    newPassword: string
  ): Promise<{ token?: string; code: number }> {
    let response: { token?: string; code: number } = {
      code: INTERNAL_ERROR_CODE,
    };

    const strength = passwordStrength(newPassword);

    if (strength.contains.length !== 4 || strength.length < 8) {
      response.code = INVALID_REQUEST_CODE;
      return response;
    }

    const user = await prisma.user.findFirst({
      where: { id: userId },
    });

    if (!user) {
      response.code = NOT_FOUND_CODE;
      return response;
    }

    if (!PasswordHashingService.compareHash(oldPassword, user.password)) {
      response.code = INVALID_REQUEST_CODE;
      return response;
    }

    const updatedUser: User = await prisma.user.update({
      where: { id: userId },
      data: {
        password: PasswordHashingService.hashPassword(newPassword),
      },
    });

    const token: string = jwt.sign(
      updatedUser,
      process.env.ACCESS_TOKEN_SECRET as string
    );

    response.code = OK_CODE;
    response.token = token;

    return response;
  }

  public static async getUserDetails(
    userId: number
  ): Promise<UserDetailResponse | null> {
    const user = await prisma.user.findFirst({
      where: { id: userId },
      include: {
        reviewee: {
          include: { reviewee: true, reviewer: true, comment: true },
        },
      },
    });

    if (!user) return null;
    return {
      reviews: user.reviewee,
      ...user,
    };
  }
}
