package com.grayseal.travelhubcompose.ui.screens.main

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.travelhubcompose.components.SearchInputField
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
        HomeScreenContentsState()
    }
}

@Preview
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenContentsState() {
    val searchState = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(searchState.value) {
        searchState.value.trim().isNotEmpty()
    }
    HomeScreenContents(
        searchState = searchState,
        keyboardController = keyboardController,
        valid = valid
    ) {searchString ->

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenContents(
    searchState: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
    valid: Boolean,
    onSearch: (String) -> Unit = {}
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
        SearchInputField(
            valueState = searchState,
            labelId = "Where to?",
            enabled = true,
            isSingleLine = false,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(searchState.value.trim())
                keyboardController?.hide()
                searchState.value = ""
            }
        )
    }
}