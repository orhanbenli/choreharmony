import express from "express";
import AuthenticationController from "./AuthenticationController";
import HouseholdController from "./HouseholdController";
import ChoreController from "./ChoreController";
import HouseholdChatController from "./HouseholdChatController";
import UserController from "./UserController";
import NotificationController from "./NotificationController";
import ReviewController from "./ReviewController";

const router = express.Router();

router.get<{}, { message: string }>("/", (req, res) => {
  res.json({
    message: "API - 👋🌎🌍🌏",
  });
});

router.use("/auth", AuthenticationController);
router.use("/household", HouseholdController);
router.use("/chore", ChoreController);
router.use("/household-chat", HouseholdChatController);
router.use("/user", UserController);
router.use("/notifications", NotificationController);
router.use("/review", ReviewController);
export default router;
