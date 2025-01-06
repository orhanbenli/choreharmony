# Chore Harmony

Chore Harmony is a multi-user Android application that helps university students manage the chores in their shared household. It is intended for the efficient tracking, and completion of chores in an equitable way for all roommates in a household. It is also meant to facilitate communication between household members. Chore Harmony helps us live together harmoniously!

<img width="293" alt="2" src="https://github.com/user-attachments/assets/f1fb91a9-b510-4c8d-9ccd-7156e83357a6" />
<img width="293" alt="4" src="https://github.com/user-attachments/assets/301b395a-e955-4ce1-8601-2eacbc59608a" />

## Features

---

**1. User Authentication:** This application should enable users to register, log in, and manage their account securely.

- Allow users to create accounts and set up profiles
- Include email verification when registering for the application

**2. Household and Chore Assignment:** Users can create or join households securely, requiring household owners to approve join requests. Once a household has been joined, recurring or one-time chores can be created and assigned to members. Approved household members can view all chores and the individual assigned to them.

**3. Household Chat:** A private household specific chat for approved household members to chat and discuss any chore related issues.

**4. Chore Tracking:** Household members will be able to mark chores as completed, which will notify all household members that it was completed and who completed it.

**5. Notification System:** The application will automatically send notifications to the user. Such as when a chore is coming due but hasn't been marked as completed.

**6. Anonymous Knock Reminders:** The application will have user initiated events to send anonymous reminders ("knocks") to roommates about pending chores. Receiving a knock from a user will reduce the user’s household power (See chore gamification).

**7. Chore gamification:** Completing chores on-time or early will reward the user with household power that improves their score. This household power represents how good of a roommate the user is. The household power accumulates and is visible to household owners before approving household join requests. Completing chores consistently will also reward the user. Essentially, actions that make one a ‘good roommate’ increases their power, while being a ‘bad roommate’ reduces their power.

**8. User Rating System:** A user can rate their household roommates, providing comments about them and what it's like living with that individual. This will be available to household owners along with the user's household power to view before they approve a specific household join request, and could be reviewed before signing a lease with this individual.

**9. Chore Trading:** Users can give chores to other household members in case they are sick or not present in the house, this transfers a specified number of household power from the user to the other roommate. Both household members involved in the ‘trade’ must accept the trade for it to take effect.

**10. User Dashboard:** A place where the user can look over the points they have accumulated and see their chore schedule. This includes integration into the device calendar, such as the ability to add a chore to their calendar for efficient planning.

## Backend

---

The backend is a RESTful API built with Node.js and Express. It's a robust API that interacts with a PostgreSQL database to store data.

### Installation

1. Enter the `/server` directory.

```
cd server
```

2. Install node modules.

```
npm install
```

3. Fill out `.env.prod` with production secrets.

```
DATABASE_URL="postgresql://..."
PORT=3000
ENVIRONMENT="PRODUCTION"
ACCESS_TOKEN_SECRET="..."
EMAIL="...@gmail.com"
EMAIL_APP_PASSWORD="xxxx xxxx xxxx xxxx"
SALT_EXPONENT=10
```

5. Optional: If the PostgreSQL database is not generated, generate the database first.

```
npm run migrateprod
```

6. Generate prisma schemas.

```
npx prisma generate
```

7. Build server.

```
npm run build
```

8. Run server.

```
npm run prod
```

## Client

---

The client is an Android application built with Kotlin. The client follows all Android development standards including a full test-suite, documentation, and releases.

### Instalation

1. Open the Android Studio project under `/app`.
2. Update the URL IP addresses _xxx.xxx.xxx.xxx_ in `gradle.properties` to match the IP address of the server you deployed earlier.
3. Build the project from Android Studio.
4. Run the project from Android Studio.
