import { Chore, User } from "@prisma/client";
import nodemailer from "nodemailer";
import { ChoreResponse } from "../dtos/ChoreResponse";

const transporter = nodemailer.createTransport({
  service: "gmail",
  host: "smtp.gmail.com",
  port: 587,
  secure: false,
  auth: {
    user: process.env.EMAIL,
    pass: process.env.EMAIL_APP_PASSWORD,
  },
});

export default class EmailService {
  private static async sendEmail(
    email: string,
    content: string,
    title: string
  ) {
    await transporter.sendMail({
      from: {
        name: "Chore Harmony",
        address: process.env.EMAIL as string,
      },
      to: email,
      subject: title,
      text: content,
    });
  }

  private static async sendBccEmails(
    emails: string[],
    content: string,
    title: string
  ) {
    if (emails.length === 0) return;
    await transporter.sendMail({
      from: {
        name: "Chore Harmony",
        address: process.env.EMAIL as string,
      },
      bcc: emails,
      subject: title,
      text: content,
    });
  }

  static async sendNewChatEmail(
    source: User,
    recipients: User[],
    message: string
  ) {
    await this.sendBccEmails(
      recipients
        .filter((x) => x.email_notifications_enabled)
        .map((x) => x.email),
      `${source.first_name} ${source.last_name} sent a message to the household chat:\n\n${message}`,
      "New Household Chat"
    );
  }

  static async sendKnockReminder(chore: ChoreResponse) {
    if (!chore.assigned_user) return;
    if (!chore.assigned_user.email_notifications_enabled) return;

    await this.sendEmail(
      chore.assigned_user.email,
      `Knock, Knock! The chore "${chore.name}" is overdue!`,
      "Chore Reminder"
    );
  }

  static async sendVerificationEmail(email: string, code: number) {
    await this.sendEmail(
      email,
      `Your verification code is: ${code.toString()}`,
      "Email Verification Code"
    );
    console.log(`CODE SENT TO ${email}: ${code}`);
  }

  static async sendChoreCompletedEmail(users: User[], chore: Chore) {
    await this.sendBccEmails(
      users.filter((x) => x.email_notifications_enabled).map((x) => x.email),
      `The chore '${chore.name}' was completed.`,
      "Completed Chore"
    );
  }

  static async sendDownloadDataEmail(user: User) {
    await transporter.sendMail({
      from: {
        name: "ChoreHarmony",
        address: process.env.EMAIL as string,
      },
      to: user.email,
      subject: "Downloaded Data",
      text: "Attached is your user data file.",
      attachments: [{ filename: "Data.json", content: JSON.stringify(user) }],
    });
  }

  static async sendTradeEmail(user: User) {
    if (!user.email_notifications_enabled) return;
    await this.sendEmail(
      user.email,
      "You received a new chore trade request!",
      "Chore Trade Request"
    );
  }
}
