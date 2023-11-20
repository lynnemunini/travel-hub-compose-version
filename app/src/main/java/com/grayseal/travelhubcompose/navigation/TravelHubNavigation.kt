package com.grayseal.travelhubcompose.navigation

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grayseal.travelhubcompose.ui.screens.details.DetailsScreen
import com.grayseal.travelhubcompose.ui.screens.main.EntriesViewModel
import com.grayseal.travelhubcompose.ui.screens.main.HomeScreen
import com.grayseal.travelhubcompose.ui.screens.signin.SignInScreen
import com.grayseal.travelhubcompose.ui.screens.signup.SignUpScreen
import com.grayseal.travelhubcompose.utils.rememberFirebaseAuthLauncher

/**
 * Composable function representing the navigation structure of the Travel Hub app.
 * It defines screens and navigation logic using Jetpack Compose and Navigation.
 */
@Composable
fun TravelHubNavigation() {
    // Set up navigation controller to manage navigation flow
    val navController = rememberNavController()

    // Obtain the current context
    val context = LocalContext.current

    // Set up Firebase authentication launcher for seamless user authentication
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberFirebaseAuthLauncher(
            onAuthComplete = {
                // Navigate to the HomeScreen upon successful authentication
                navController.navigate(TravelHubScreens.HomeScreen.name)
            },
            onAuthError = {
                // Display an error toast if authentication fails
                Toast.makeText(context, "${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        )

    // Set up the EntriesViewModel to manage data for the app
    val entriesViewModel: EntriesViewModel = viewModel()

    // Define the navigation structure using Jetpack Compose NavHost
    NavHost(navController = navController, startDestination = TravelHubScreens.HomeScreen.name) {
        composable(TravelHubScreens.SignUpScreen.name) {
            SignUpScreen(navController = navController, launcher)
        }

        composable(TravelHubScreens.SignInScreen.name) {
            SignInScreen(navController = navController, launcher)
        }

        composable(TravelHubScreens.HomeScreen.name) {
            HomeScreen(navController = navController, entriesViewModel)
        }

        val route = TravelHubScreens.DetailsScreen.name
        composable("$route/{id}", arguments = listOf(navArgument(name = "id") {
            type = NavType.StringType
        })) { navBack ->
            navBack.arguments?.getString("id").let { id ->
                DetailsScreen(navController = navController, entriesViewModel, id = id)
            }
        }
    }
}
