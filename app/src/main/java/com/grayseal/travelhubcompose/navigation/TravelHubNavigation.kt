package com.grayseal.travelhubcompose.navigation

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grayseal.travelhubcompose.ui.screens.details.DetailsScreen
import com.grayseal.travelhubcompose.ui.screens.main.HomeScreen
import com.grayseal.travelhubcompose.ui.screens.signin.SignInScreen
import com.grayseal.travelhubcompose.ui.screens.signup.SignUpScreen
import com.grayseal.travelhubcompose.utils.rememberFirebaseAuthLauncher

@Composable
fun TravelHubNavigation() {
    val navController = rememberNavController()
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberFirebaseAuthLauncher(
            onAuthComplete = { },
            onAuthError = { }
        )
    NavHost(navController = navController, startDestination = TravelHubScreens.HomeScreen.name) {
        composable(TravelHubScreens.SignUpScreen.name) {
            SignUpScreen(navController = navController, launcher)
        }
        composable(TravelHubScreens.SignInScreen.name) {
            SignInScreen(navController = navController, launcher)
        }
        composable(TravelHubScreens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
        composable(TravelHubScreens.DetailsScreen.name) {
            DetailsScreen(navController = navController)
        }
    }
}