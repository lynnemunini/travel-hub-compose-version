package com.grayseal.travelhubcompose.ui.screens.signup


import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.grayseal.travelhubcompose.utils.isEmailValid
import com.grayseal.travelhubcompose.utils.isPasswordValid
import kotlinx.coroutines.tasks.await

class SignUpViewModel : ViewModel() {
    suspend fun registerUser(email: String, password: String): RegistrationResult {
        if (!isEmailValid(email)) {
            return RegistrationResult.InvalidEmail
        }

        if (!isPasswordValid(password)) {
            return RegistrationResult.InvalidPassword
        }

        return try {
            val authResult =
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                RegistrationResult.Success
            } else {
                RegistrationResult.Failure("Registration failed")
            }
        } catch (e: Exception) {
            RegistrationResult.Failure(e.message)
        }
    }
}


sealed class RegistrationResult {
    object Success : RegistrationResult()
    object InvalidEmail : RegistrationResult()
    object InvalidPassword : RegistrationResult()
    data class Failure(val error: String?) : RegistrationResult()
}