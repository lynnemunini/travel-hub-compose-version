package com.grayseal.travelhubcompose.ui.screens.main

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.travelhubcompose.R
import com.grayseal.travelhubcompose.components.SearchInputField
import com.grayseal.travelhubcompose.data.model.TravelItem
import com.grayseal.travelhubcompose.navigation.TravelHubScreens
import com.grayseal.travelhubcompose.ui.screens.signin.SignInScreen
import com.grayseal.travelhubcompose.ui.theme.Grey200
import com.grayseal.travelhubcompose.ui.theme.Yellow200
import com.grayseal.travelhubcompose.ui.theme.manropeFamily
import com.grayseal.travelhubcompose.utils.rememberFirebaseAuthLauncher
import com.grayseal.travelhubcompose.utils.toTitleCase

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
        HomeScreenContentsState(entriesViewModel, navController)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenContentsState(entriesViewModel: EntriesViewModel, navController: NavController) {
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
        navController,
        searchState = searchState,
        keyboardController = keyboardController,
        valid = valid,
        loading,
        filteredItems.shuffled(),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenContents(
    navController: NavController,
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
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp)
        ) {
            items(travelItems.size) { travelItem ->
                TravelItemCard(travelItems[travelItem], navController)
            }
        }
    }
}

@Composable
fun TravelItemCard(travelItem: TravelItem, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    radius = Dp.Hairline
                ),
                onClick = {
                    navController.navigate(route = TravelHubScreens.DetailsScreen.name + "/${travelItem._id}")
                }
            )
            .padding(bottom = 20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(travelItem.photos[1])
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder),
                contentDescription = "House",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        FavoriteContainer(travelItem = travelItem, modifier = Modifier.align(Alignment.TopStart))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .height(90.dp)
                .align(Alignment.BottomStart),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(1.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Text(
                    text = toTitleCase(travelItem.name),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = manropeFamily
                    )
                )
                Text(
                    text = toTitleCase(travelItem.location.name),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        fontFamily = manropeFamily
                    ),
                    color = Color.Gray
                )
                RatingPriceView(travelItem)
            }
        }
    }
}

@Composable
fun RatingPriceView(travelItem: TravelItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Stars",
                    modifier = Modifier
                        .size(22.dp)
                )

                Text(
                    text = "${travelItem.rating} Rating",
                    fontFamily = manropeFamily,
                    fontSize = 14.sp,
                    modifier = Modifier
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
        ) {
            Row {
                Text(
                    text = "${travelItem.price.currency} ${travelItem.price.amount}",
                    fontFamily = manropeFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = "/night",
                    fontFamily = manropeFamily,
                    color = Grey200,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }
        }
    }
}

@Composable
fun FavoriteContainer(travelItem: TravelItem, modifier: Modifier) {
    var isFavorited by remember { mutableStateOf(false) }

    val iconResId = if (isFavorited) {
        R.drawable.filled_favorite
    } else {
        R.drawable.favorite
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black)
        ) {
            Text(
                text = travelItem.uniqueType,
                fontFamily = manropeFamily,
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .background(shape = CircleShape, color = Color.Transparent)
                .align(Alignment.CenterEnd)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        radius = Dp.Hairline
                    ),
                    onClick = {
                        isFavorited = !isFavorited
                    }
                )
                .size(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(shape = CircleShape, color = MaterialTheme.colorScheme.background)
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "Favourite",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(26.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}