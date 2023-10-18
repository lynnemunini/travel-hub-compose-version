package com.grayseal.travelhubcompose.ui.screens.details

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grayseal.travelhubcompose.R
import com.grayseal.travelhubcompose.data.model.DetailsDrawable
import com.grayseal.travelhubcompose.data.model.TravelItem
import com.grayseal.travelhubcompose.ui.screens.main.EntriesViewModel
import com.grayseal.travelhubcompose.ui.theme.Yellow200
import com.grayseal.travelhubcompose.ui.theme.manropeFamily
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
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalImagePager(navController, images = travelItem.photos)
        Details()
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
                .height(220.dp)
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

@Composable
fun Details() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "Olohoro Ndogo - a romantic Rift Valley retreat",
            fontFamily = manropeFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Stars",
                    modifier = Modifier
                        .size(22.dp)
                )

                Text(
                    text = "4.5",
                    fontFamily = manropeFamily,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "•",
                fontFamily = manropeFamily,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.reviews),
                    contentDescription = "Stars",
                    modifier = Modifier
                        .size(18.dp)
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("5 reviews")
                        }
                    },
                    fontFamily = manropeFamily,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Text(
                text = "•",
                fontFamily = manropeFamily,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            Text(
                text = "Kajiado County, Kenya",
                fontFamily = manropeFamily,
                fontSize = 14.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Entire home hosted by Andrew Letman",
                fontFamily = manropeFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.weight(2.5f)
            )

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://storage.googleapis.com/leizen-frontend.appspot.com/hotel/211cf36fe68a08d480e520920e8184e7?GoogleAccessId=firebase-adminsdk-pe7p9%40leizen-frontend.iam.gserviceaccount.com&Expires=16749763200&Signature=WRYINQ3MPLt%2FyMpuAN0WkIiy%2B%2BZE6vf69mDV8hvVtJQb1NEJ33I9Y2VD6RsXn431T%2B04%2Fp%2FNoEwTSte4w5VADev4xRlWTZvc5EBQxCBnYNpTmfl5A6608XzyICFied0%2B9ym%2FB4krCDtgXxQpD2nac%2FBTGWmqvYqWpFHmyLdFyVulhPlXtWOlxQ2MyP0%2FkpramA3o%2B0TA4utBJM%2F%2BUu21Ogh8YXzqcE%2B093Jxj%2BXA7js0Bb4%2FIkPhxSWKk4JYcTMEFVWBWSBZ2bBvZlVeHRDtX%2Bz%2BGVY7FFKocKTHtTAGnqdvCZoQJs8uAqaBem%2BEzDNdAqJ%2FrxFcR4xC%2FGpikMg%2BXg%3D%3D")
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder),
                contentDescription = "House",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.5f)
                    .size(50.dp)
                    .padding(start = 6.dp)
                    .clip(CircleShape)
            )
        }
        Divider(
            color = Color.LightGray,
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        TravelItemDetails()
        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        EmergencyBookingDetails()
        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
    }
}

@Composable
fun TravelItemDetails() {
    val items = listOf(
        DetailsDrawable("bath", R.drawable.shower, "2 bath"),
        DetailsDrawable("bed", R.drawable.bed, "2 bed"),
        DetailsDrawable("bedroom", R.drawable.bedroom, "2 bedroom"),
        DetailsDrawable("guests", R.drawable.guests, "2 guests")
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 6.dp)
    ) {
        items(items.size) { item ->
            ItemCard(items[item])
        }
    }
}

@Preview
@Composable
fun EmergencyBookingDetails() {
    Column(
        modifier = Modifier.padding(horizontal = 30.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.door),
                contentDescription = "Icon",
                modifier = Modifier
                    .size(32.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Self check-in",
                    fontFamily = manropeFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "True",
                    fontFamily = manropeFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.discount),
                contentDescription = "Icon",
                modifier = Modifier
                    .size(32.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Discounted Percentage",
                    fontFamily = manropeFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "10",
                    fontFamily = manropeFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.door),
                contentDescription = "Icon",
                modifier = Modifier
                    .size(32.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Cancellation Policy",
                    fontFamily = manropeFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Flexible",
                    fontFamily = manropeFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ItemCard(item: DetailsDrawable) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .padding(top = 4.dp, start = 20.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = "Icon",
                modifier = Modifier
                    .size(26.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = item.text,
                fontFamily = manropeFamily,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
