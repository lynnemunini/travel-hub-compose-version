package com.grayseal.travelhubcompose.utils

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Checks if the given email address is valid based on a regular expression pattern.
 *
 * @param email The email address to validate.
 * @return True if the email is valid, false otherwise.
 */
fun isEmailValid(email: String): Boolean {
    val regexPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return email.matches(Regex(regexPattern))
}

/**
 * Checks if the given password is valid based on a length criterion.
 *
 * @param password The password to validate.
 * @return True if the password is valid, false otherwise.
 */
fun isPasswordValid(password: String): Boolean {
    return password.length >= 6
}

fun toTitleCase(input: String): String {
    val words = input.split(" ")
    val titleCaseWords = words.map { word ->
        word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()
    }
    return titleCaseWords.joinToString(" ") // Join the words back together with spaces
}

/**

A composable function that returns a [ManagedActivityResultLauncher] to launch the Firebase authentication flow
with Google Sign-In provider. This function handles the result of the authentication flow and calls the
[onAuthComplete] callback if the authentication is successful, or [onAuthError] if an error occurs.
 * @param onAuthComplete a lambda function that will be called with the [AuthResult] if the authentication is successful.
 * @param onAuthError a lambda function that will be called with the [ApiException] if an error occurs during the authentication flow.
 * @return a [ManagedActivityResultLauncher] instance that can be used to launch the Firebase authentication flow with Google Sign-In provider.
 */

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}