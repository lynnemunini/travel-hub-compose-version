package com.grayseal.travelhubcompose.ui.screens.main

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.travelhub.data.model.TravelItem
import com.grayseal.travelhubcompose.components.SearchInputField
import com.grayseal.travelhubcompose.navigation.TravelHubScreens
import com.grayseal.travelhubcompose.ui.screens.signin.SignInScreen
import com.grayseal.travelhubcompose.ui.theme.Yellow200
import com.grayseal.travelhubcompose.utils.rememberFirebaseAuthLauncher

@Composable
fun HomeScreen(navController: NavController, entriesViewModel: EntriesViewModel) {
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
        HomeScreenContentsState(entriesViewModel)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenContentsState(entriesViewModel: EntriesViewModel) {
    val searchState = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(searchState.value) {
        searchState.value.trim().isNotEmpty()
    }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var travelItems by remember {
        mutableStateOf(listOf<TravelItem>())
    }
    val filteredItems = remember(searchState.value, travelItems) {
        travelItems.filter { travelItem ->
            searchState.value.isBlank() || travelItem.location.name.contains(
                searchState.value,
                ignoreCase = true
            )
        }
    }

    LaunchedEffect(entriesViewModel) {
        loading = true
        entriesViewModel.getAllEntries(context, callback = { items ->
            travelItems = items
            loading = false
        })
    }
    HomeScreenContents(
        searchState = searchState,
        keyboardController = keyboardController,
        valid = valid,
        loading,
        filteredItems,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenContents(
    searchState: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
    valid: Boolean,
    loading: Boolean,
    travelItems: List<TravelItem>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
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
                keyboardController?.hide()
                searchState.value = ""
            }
        )
        if (loading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Yellow200,
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(travelItems.size) { travelItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("HELLO")
                }
            }
        }
    }
}