package com.grayseal.travelhubcompose.ui.screens.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grayseal.travelhub.data.model.TravelItem
import com.grayseal.travelhubcompose.R
import com.grayseal.travelhubcompose.ui.screens.main.EntriesViewModel
import com.grayseal.travelhubcompose.ui.theme.Yellow200
import kotlin.math.absoluteValue

@Composable
fun DetailsScreen(navController: NavController, entriesViewModel: EntriesViewModel, id: String?) {
    val context = LocalContext.current
    var loading by remember {
        mutableStateOf(true)
    }
    var travelItem: TravelItem? by remember {
        mutableStateOf(null)
    }
    if (id != null) {
        entriesViewModel.getEntryById(id, context) { item ->
            loading = false
            travelItem = item
        }
    }
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
    } else {
        travelItem?.let { DetailsScreenElements(navController, travelItem = it) }
    }
}


@Composable
fun DetailsScreenElements(navController: NavController, travelItem: TravelItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalImagePager(navController, images = travelItem.photos)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalImagePager(navController: NavController, images: List<String>) {
    val pagerState = rememberPagerState(pageCount = {
        images.size
    })

    HorizontalPager(state = pagerState) { page ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .graphicsLayer {
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[page])
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder),
                contentDescription = "House",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            FavoriteContainer(
                navController, modifier = Modifier
                    .align(Alignment.TopStart)
            )
            Row(
                Modifier
                    .height(50.dp)
                    .padding(bottom = 8.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                repeat(images.size) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Yellow200 else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteContainer(navController: NavController, modifier: Modifier) {
    var isFavorited by remember { mutableStateOf(false) }

    val iconResId = if (isFavorited) {
        R.drawable.filled_favorite
    } else {
        R.drawable.favorite
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(shape = CircleShape, color = Color.Transparent)
                .align(Alignment.CenterStart)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        radius = Dp.Hairline
                    ),
                    onClick = {
                        navController.popBackStack()
                    }
                )
                .size(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(shape = CircleShape, color = MaterialTheme.colorScheme.background)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(22.dp)
                        .align(Alignment.Center)
                )
            }
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