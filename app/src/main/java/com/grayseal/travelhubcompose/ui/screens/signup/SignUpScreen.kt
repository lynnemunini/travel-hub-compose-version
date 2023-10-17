package com.grayseal.travelhubcompose.ui.screens.signup

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.grayseal.travelhubcompose.ContinueWithGoogle
import com.grayseal.travelhubcompose.EmailInput
import com.grayseal.travelhubcompose.PasswordInput
import com.grayseal.travelhubcompose.R
import com.grayseal.travelhubcompose.SubmitButton
import com.grayseal.travelhubcompose.navigation.TravelHubScreens
import com.grayseal.travelhubcompose.ui.theme.Yellow200
import com.grayseal.travelhubcompose.ui.theme.manropeFamily
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    navController: NavController,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    UserForm(navController, launcher)
}

@Composable
fun UserForm(
    navController: NavController,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val signUpViewModel: SignUpViewModel = viewModel()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val context = LocalContext.current
    val token = stringResource(id = R.string.server_client_id)

    var registrationResult by remember { mutableStateOf<RegistrationResult?>(null) }
    var loading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    UIComponents(
        navController,
        email = email,
        password = password,
        loading = loading,
        passwordVisibility = passwordVisibility,
        passwordFocusRequest = passwordFocusRequest,
        continueWithGoogle = {
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(token)
                    .requestEmail()
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        },
        submit = {
            val userEmail = email.value
            val userPassword = password.value

            loading = true

            coroutineScope.launch {
                registrationResult = signUpViewModel.registerUser(userEmail, userPassword)
                loading = false
            }
        }
    )


    registrationResult?.let { result ->
        when (result) {
            is RegistrationResult.Success -> {
                navController.navigate(TravelHubScreens.HomeScreen.name)
            }

            is RegistrationResult.InvalidEmail -> {
                Toast.makeText(context, "Invalid email", Toast.LENGTH_LONG)
                    .show()
            }

            is RegistrationResult.InvalidPassword -> {
                Toast.makeText(
                    context,
                    "Password must be at least 6 characters long",
                    Toast.LENGTH_LONG
                ).show()
            }

            is RegistrationResult.Failure -> {
                Toast.makeText(
                    context,
                    "Registration failed: ${result.error}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
fun UIComponents(
    navController: NavController,
    email: MutableState<String>,
    password: MutableState<String>,
    loading: Boolean,
    passwordVisibility: MutableState<Boolean>,
    passwordFocusRequest: FocusRequester,
    continueWithGoogle: () -> Unit,
    submit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.travel_hub),
            textAlign = TextAlign.Center,
            fontFamily = manropeFamily,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(id = R.string.your_ultimate_companion_for_seamless_travel_experiences),
            fontFamily = manropeFamily,
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground
        )
        Image(
            painter = painterResource(id = R.drawable.travel),
            contentDescription = "Travel Image",
            Modifier
                .width(300.dp)
                .height(300.dp)
                .padding(vertical = 20.dp)
        )
        EmailInput(emailState = email)
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            passwordVisibility = passwordVisibility,
        )
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(38.dp)
                    .height(38.dp),
                color = Yellow200,
            )
        }
        SubmitButton(text = stringResource(id = R.string.sign_up), submit)
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("Already have an account? ")
                withStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = Yellow200,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Sign in")
                }
            }
        }
        Text(
            text = annotatedString,
            fontFamily = manropeFamily,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable { navController.navigate(TravelHubScreens.SignInScreen.name) }
        )
        ContinueWithGoogle(stringResource(id = R.string.sign_up_with_google), continueWithGoogle)
    }
}
