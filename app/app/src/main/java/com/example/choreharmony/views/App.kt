package com.example.choreharmony.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.choreharmony.viewmodel.HomeViewModel
import com.example.choreharmony.views.assets.ChoreHarmonyNavigationBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (homeViewModel.userRepository.currentUser.value?.token != null) {
                ChoreHarmonyNavigationBar(navController = navController)
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = Color.White
        ) {
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginView(navController = navController)
                }
                composable("home") {
                    HomeView(navController = navController)
                }
                composable("signup") {
                    SignUpView(navController = navController)
                }
                composable("verification") {
                    EmailVerificationView(navController = navController)
                }
                composable("chat") {
                    HouseholdChatView(navController = navController)
                }
                composable("create-household") {
                    CreateHouseholdView(navController = navController)
                }
                composable("join-household") {
                    JoinHouseholdView(navController = navController)
                }
                composable("manage-my-pending-requests") {
                    ManageSentJoinRequestsViewModel(navController = navController)
                }
                composable("roommates") {
                    RoommatesView(navController = navController)
                }
                composable("create-chore") {
                    CreateChoreView(navController = navController)
                }
                composable("settings") {
                    SettingsView(navController = navController)
                }
                composable("chore-details/{cId}") { navBackStackEntry ->
                    val cId = navBackStackEntry.arguments?.getString( "cId" )
                    cId?.let {
                        ChoreDetailsView(navController = navController, cId=cId!!.toInt())
                    }
                }
                composable("notifications") {
                    NotificationsView(navController = navController)
                }
                composable("change-password") {
                    ChangePasswordView(navController = navController)
                }
                composable("trade-chore/{choreId}") {navBackStackEntry ->
                    val cId = navBackStackEntry.arguments?.getString( "choreId" )
                    cId?.let {
                        TradeChoreView(navController = navController, choreId= cId.toInt())
                    }
                }
                composable("trade-requests") {
                    TradeRequestsView(navController = navController)
                }
                composable("user/{userId}/{approved}") {navBackStackEntry ->
                    val uId = navBackStackEntry.arguments?.getString( "userId" )
                    val approved = navBackStackEntry.arguments?.getString("approved")

                    uId?.let {
                        UserDetailView(navController = navController,
                            userId= uId.toInt(),
                            approved = approved == "approved")
                    }
                }
                composable("reassign-chore/{choreId}") {navBackStackEntry ->
                    val cId = navBackStackEntry.arguments?.getString( "choreId" )
                    cId?.let {
                        ReassignChoreView(navController = navController,
                            choreId= cId.toInt())
                    }
                }
                composable("review-user/{userId}") {navBackStackEntry ->
                    val uId = navBackStackEntry.arguments?.getString( "userId" )
                    uId?.let {
                        ReviewUserView(navController = navController,
                            userId= uId.toInt())
                    }
                }
            }
        }
    }
}