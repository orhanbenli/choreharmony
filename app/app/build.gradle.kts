plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.choreharmony"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.choreharmony"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildFeatures {
            buildConfig = true
        }

        val loginApi = properties["LOGIN_URL"]?.toString()
        if (loginApi != null) {
            buildConfigField("String", "LOGIN_URL", loginApi)
        }

        val registerApi = properties["REGISTER_API_URL"]?.toString()
        if (registerApi != null) {
            buildConfigField("String", "REGISTER_API_URL", registerApi)
        }

        val emailVerificationApi = properties["EMAIL_VERIFICATION_URL"]?.toString()
        if (emailVerificationApi != null) {
            buildConfigField("String", "EMAIL_VERIFICATION_URL", emailVerificationApi)
        }

        val getMyHouseholdUrl = properties["GET_MY_HOUSEHOLD_URL"]?.toString()
        if (getMyHouseholdUrl != null) {
            buildConfigField("String", "GET_MY_HOUSEHOLD_URL", getMyHouseholdUrl)
        }

        val createHouseholdUrl = properties["CREATE_HOUSEHOLD_URL"]?.toString()
        if (createHouseholdUrl != null) {
            buildConfigField("String", "CREATE_HOUSEHOLD_URL", createHouseholdUrl)
        }

        val sendHouseholdJoinRequestUrl = properties["SEND_HOUSEHOLD_JOIN_REQUEST"]?.toString()
        if (sendHouseholdJoinRequestUrl != null) {
            buildConfigField("String", "SEND_HOUSEHOLD_JOIN_REQUEST", sendHouseholdJoinRequestUrl)
        }

        val getSentHouseholdJoinRequestUrl = properties["GET_SENT_HOUSEHOLD_JOIN_REQUESTS"]?.toString()
        if (getSentHouseholdJoinRequestUrl != null) {
            buildConfigField("String", "GET_SENT_HOUSEHOLD_JOIN_REQUESTS", getSentHouseholdJoinRequestUrl)
        }

        val manageHouseholdJoinRequestUrl = properties["MANAGE_HOUSEHOLD_JOIN_REQUEST_URL"]?.toString()
        if (manageHouseholdJoinRequestUrl != null) {
            buildConfigField(
                "String",
                "MANAGE_HOUSEHOLD_JOIN_REQUEST_URL",
                manageHouseholdJoinRequestUrl
            )
        }

        val pendingHouseholdJoinRequests = properties["GET_PENDING_HOUSEHOLD_JOIN_REQUESTS"]?.toString()
        if (pendingHouseholdJoinRequests != null) {
            buildConfigField(
                "String",
                "GET_PENDING_HOUSEHOLD_JOIN_REQUESTS",
                pendingHouseholdJoinRequests
            )
        }

        val getHouseholdChores = properties["GET_HOUSEHOLD_CHORES"]?.toString()
        if (getHouseholdChores != null) {
            buildConfigField(
                "String",
                "GET_HOUSEHOLD_CHORES",
                getHouseholdChores
            )
        }

        val getMyChores = properties["GET_MY_CHORES"]?.toString()
        if (getMyChores != null) {
            buildConfigField(
                "String",
                "GET_MY_CHORES",
                getMyChores
            )
        }

        val completeChoreURL = properties["COMPLETE_CHORE_URL"]?.toString()
        if (completeChoreURL != null) {
            buildConfigField(
                "String",
                "COMPLETE_CHORE_URL",
                completeChoreURL
            )
        }

        val createChoreUrl = properties["CREATE_CHORE_URL"]?.toString()
        if (createChoreUrl != null) {
            buildConfigField(
                "String",
                "CREATE_CHORE_URL",
                createChoreUrl
            )
        }

        val getChoreAssignableMembers = properties["GET_CHORE_ASSIGNABLE_MEMBERS_URL"]?.toString()
        if (getChoreAssignableMembers != null) {
            buildConfigField(
                "String",
                "GET_CHORE_ASSIGNABLE_MEMBERS_URL",
                getChoreAssignableMembers
            )
        }

        val deleteHouseholdUrl = properties["DELETE_HOUSEHOLD_URL"]?.toString()
        if (deleteHouseholdUrl != null) {
            buildConfigField(
                "String",
                "DELETE_HOUSEHOLD_URL",
                deleteHouseholdUrl
            )
        }

        val leaveHouseholdUrl = properties["LEAVE_HOUSEHOLD_URL"]?.toString()
        if (leaveHouseholdUrl != null) {
            buildConfigField(
                "String",
                "LEAVE_HOUSEHOLD_URL",
                leaveHouseholdUrl
            )
        }

        val householdChatsUrl = properties["HOUSEHOLD_CHATS_URL"]?.toString()
        if (householdChatsUrl != null) {
            buildConfigField(
                "String",
                "HOUSEHOLD_CHATS_URL",
                householdChatsUrl
            )
        }

        val getChoreUrl = properties["GET_CHORE"]?.toString()
        if (getChoreUrl != null) {
            buildConfigField(
                "String",
                "GET_CHORE",
                getChoreUrl
            )
        }

        val deleteAccountUrl = properties["DELETE_ACCOUNT_URL"]?.toString()
        if (deleteAccountUrl != null) {
            buildConfigField(
                "String",
                "DELETE_ACCOUNT_URL",
                deleteAccountUrl
            )
        }

        val knockChoreUrl = properties["KNOCK_CHORE_URL"]?.toString()
        if (knockChoreUrl != null) {
            buildConfigField(
                "String",
                "KNOCK_CHORE_URL",
                knockChoreUrl
            )
        }

        val getNotificationsUrl = properties["GET_NOTIFICATIONS_URL"]?.toString()
        if (getNotificationsUrl != null) {
            buildConfigField(
                "String",
                "GET_NOTIFICATIONS_URL",
                getNotificationsUrl
            )
        }

        val deleteNotificationUrl = properties["DELETE_NOTIFICATION_URL"]?.toString()
        if (deleteNotificationUrl != null) {
            buildConfigField(
                "String",
                "DELETE_NOTIFICATION_URL",
                deleteNotificationUrl
            )
        }

        val downloadDataUrl = properties["DOWNLOAD_DATA_URL"]?.toString()
        if (downloadDataUrl != null) {
            buildConfigField(
                "String",
                "DOWNLOAD_DATA_URL",
                downloadDataUrl
            )
        }

        val changePasswordUrl = properties["CHANGE_PASSWORD_URL"]?.toString()
        if (changePasswordUrl != null) {
            buildConfigField(
                "String",
                "CHANGE_PASSWORD_URL",
                changePasswordUrl
            )
        }

        val emailNotificationsUrl = properties["EMAIL_NOTIFICATIONS_URL"]?.toString()
        if (emailNotificationsUrl != null) {
            buildConfigField(
                "String",
                "EMAIL_NOTIFICATIONS_URL",
                emailNotificationsUrl
            )
        }

        val createTradeUrl = properties["CREATE_TRADE_URL"]?.toString()
        if (createTradeUrl != null) {
            buildConfigField(
                "String",
                "CREATE_TRADE_URL",
                createTradeUrl
            )
        }

        val sentTradeRequestUrl = properties["SENT_TRADE_REQUESTS_URL"]?.toString()
        if (sentTradeRequestUrl != null) {
            buildConfigField(
                "String",
                "SENT_TRADE_REQUESTS_URL",
                sentTradeRequestUrl
            )
        }

        val pendingTradeRequestsUrl = properties["PENDING_TRADE_REQUESTS_URL"]?.toString()
        if (pendingTradeRequestsUrl != null) {
            buildConfigField(
                "String",
                "PENDING_TRADE_REQUESTS_URL",
                pendingTradeRequestsUrl
            )
        }

        val getUserDetailsUrl = properties["GET_USER_DETAILS_URL"]?.toString()
        if (getUserDetailsUrl != null) {
            buildConfigField(
                "String",
                "GET_USER_DETAILS_URL",
                getUserDetailsUrl
            )
        }

        val reassignChoreUrl = properties["REASSIGN_CHORE_URL"]?.toString()
        if (reassignChoreUrl != null) {
            buildConfigField(
                "String",
                "REASSIGN_CHORE_URL",
                reassignChoreUrl
            )
        }

        val getCommentsUrl = properties["GET_ALL_COMMENTS_URL"]?.toString()
        if (getCommentsUrl != null) {
            buildConfigField(
                "String",
                "GET_ALL_COMMENTS_URL",
                getCommentsUrl
            )
        }

        val postReviewUrl = properties["POST_REVIEW_URL"]?.toString()
        if (postReviewUrl != null) {
            buildConfigField(
                "String",
                "POST_REVIEW_URL",
                postReviewUrl
            )
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.4.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-core:1.6.3")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    implementation("com.auth0:java-jwt:3.18.2")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material:material:1.6.3")
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}