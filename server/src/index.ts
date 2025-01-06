import ms from "ms";
import app from "./app";
import ChoreService from "./services/ChoreService";

const port = process.env.PORT || 5000;
app.listen(port, () => {
  console.log(`Listening: http://localhost:${port}`);
});

setInterval(async () => {
  console.log("Sending reminders ---");
  await ChoreService.sendReminders();
  console.log("Sent all reminders ---");
}, ms("1d"));
