package com.grayseal.travelhubcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grayseal.travelhubcompose.ui.screens.details.DetailsScreen
import com.grayseal.travelhubcompose.ui.screens.main.HomeScreen
import com.grayseal.travelhubcompose.ui.screens.signin.SignInScreen
import com.grayseal.travelhubcompose.ui.screens.signup.SignUpScreen

@Composable
fun TravelHubNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = TravelHubScreens.SignUpScreen.name) {

        composable(TravelHubScreens.SignInScreen.name) {
            SignInScreen(navController = navController)
        }
        composable(TravelHubScreens.SignUpScreen.name) {
            SignUpScreen(navController = navController)
        }
        composable(TravelHubScreens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
        composable(TravelHubScreens.DetailsScreen.name) {
            DetailsScreen(navController = navController)
        }
    }
}