package com.grayseal.travelhubcompose.navigation

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberFirebaseAuthLauncher(
            onAuthComplete = {
                navController.navigate(TravelHubScreens.HomeScreen.name)
            },
            onAuthError = {
                Toast.makeText(context, "${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
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