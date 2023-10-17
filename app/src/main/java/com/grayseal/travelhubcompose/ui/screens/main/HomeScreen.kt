package com.grayseal.travelhubcompose.ui.screens.main

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.travelhubcompose.navigation.TravelHubScreens
import com.grayseal.travelhubcompose.ui.screens.signin.SignInScreen
import com.grayseal.travelhubcompose.utils.rememberFirebaseAuthLauncher

@Composable
fun HomeScreen(navController: NavController) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberFirebaseAuthLauncher(
            onAuthComplete = { result ->
                user = result.user
                navController.navigate(TravelHubScreens.HomeScreen.name)
            },
            onAuthError = {
                user = null
            }
        )
    if (user == null) {
        SignInScreen(navController, launcher)
    } else {

    }
}