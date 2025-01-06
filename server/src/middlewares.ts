import { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";
import { UNAUTHORIZED_CODE } from "./lib/StatusCodes";

export function authenticateToken(
  req: Request | any,
  res: Response,
  next: NextFunction
) {
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];
  if (!token) return res.sendStatus(UNAUTHORIZED_CODE);

  jwt.verify(
    token,
    process.env.ACCESS_TOKEN_SECRET as string,
    (err: any, user: any) => {
      if (err) return res.sendStatus(UNAUTHORIZED_CODE);
      req.body.user = user;
      next();
    }
  );
}
