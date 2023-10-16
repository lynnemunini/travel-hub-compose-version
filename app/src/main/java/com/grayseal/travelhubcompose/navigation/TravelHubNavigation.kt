package com.grayseal.travelhubcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grayseal.travelhubcompose.ui.screens.SplashScreen
import com.grayseal.travelhubcompose.ui.screens.details.DetailsScreen
import com.grayseal.travelhubcompose.ui.screens.main.HomeScreen
import com.grayseal.travelhubcompose.ui.screens.signin.SignInScreen
import com.grayseal.travelhubcompose.ui.screens.signup.SignUpScreen

@Composable
fun TravelHubNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = TravelHubScreens.SplashScreen.name) {
        composable(TravelHubScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(TravelHubScreens.SplashScreen.name) {
            SignInScreen(navController = navController)
        }
        composable(TravelHubScreens.SplashScreen.name) {
            SignUpScreen(navController = navController)
        }
        composable(TravelHubScreens.SplashScreen.name) {
            HomeScreen(navController = navController)
        }
        composable(TravelHubScreens.SplashScreen.name) {
            DetailsScreen(navController = navController)
        }
    }
}