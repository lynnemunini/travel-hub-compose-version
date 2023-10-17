package com.grayseal.travelhubcompose.ui.screens.signin

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel() {
    suspend fun signInUser(email: String, password: String): SignInResult {

        return try {
            val authResult =
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                SignInResult.Success
            } else {
                SignInResult.Failure("Signin failed")
            }
        } catch (e: Exception) {
            SignInResult.Failure(e.message)
        }
    }
}


/**
 * Sealed class representing the possible results of user sign-in
 */
sealed class SignInResult {
    object Success : SignInResult()
    data class Failure(val error: String?) : SignInResult()
}