package com.grayseal.travelhubcompose.ui.screens.details

import android.view.LayoutInflater
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.grayseal.travelhubcompose.R
import com.grayseal.travelhubcompose.data.model.DetailsDrawable
import com.grayseal.travelhubcompose.data.model.TravelItem
import com.grayseal.travelhubcompose.ui.screens.main.EntriesViewModel
import com.grayseal.travelhubcompose.ui.theme.Yellow200
import com.grayseal.travelhubcompose.ui.theme.manropeFamily
import com.grayseal.travelhubcompose.utils.CalendarDecorator
import com.grayseal.travelhubcompose.utils.toTitleCase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
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
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalImagePager(navController, images = travelItem.photos)
            Details(travelItem)
        }
        ReserveCard(travelItem = travelItem)
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
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
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
fun Details(travelItem: TravelItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = toTitleCase(travelItem.name),
            fontFamily = manropeFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp)
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
                    text = "${travelItem.rating} Rating",
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
                    contentDescription = "Reviews",
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
                            append("${travelItem.reviews.size} reviews")
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = "Location",
                    modifier = Modifier
                        .size(18.dp)
                )

                Text(
                    text = toTitleCase(travelItem.location.name),
                    fontFamily = manropeFamily,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${travelItem.space} hosted by ${toTitleCase(travelItem.user.firstName + " ${travelItem.user.lastName}")}",
                fontFamily = manropeFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.weight(2.5f)
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(travelItem.user.profilePictureURL)
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
        if (travelItem.tags.isNotEmpty()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.tag),
                    contentDescription = "Tags",
                    tint = Yellow200,
                    modifier = Modifier
                        .size(18.dp)
                )

                Text(
                    text = travelItem.tags.joinToString(", "),
                    fontFamily = manropeFamily,
                    fontSize = 12.sp,
                    style = TextStyle(fontStyle = FontStyle.Italic),
                    maxLines = 1,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Divider(
            color = Color.LightGray,
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        TravelItemDetails(travelItem)
        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        EmergencyBookingDetails(travelItem)
        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        Description(travelItem)
        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        LocationAddress(travelItem)
        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        HouseRulesDetails(travelItem)
        Divider(
            color = Color.LightGray.copy(alpha = 0.6f),
            thickness = 0.4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
        )
        CalendarView(travelItem)
    }
}

@Composable
fun TravelItemDetails(travelItem: TravelItem) {
    val items = listOf(
        DetailsDrawable("bath", R.drawable.shower, "${travelItem.details.bath}"),
        DetailsDrawable("bed", R.drawable.bed, "${travelItem.details.beds}"),
        DetailsDrawable("bedroom", R.drawable.bedroom, "${travelItem.details.bedroom}"),
        DetailsDrawable("guests", R.drawable.guests, "${travelItem.details.guests}")
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

@Composable
fun EmergencyBookingDetails(travelItem: TravelItem) {
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
                    text = toTitleCase(travelItem.emergencyBooking.selfCheckin.toString()),
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
                    text = "${travelItem.emergencyBooking.discountPercentage}% ",
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
                    text = travelItem.cancellationPolicy,
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
fun Description(travelItem: TravelItem) {
    val amenities = travelItem.amenities.joinToString(", ")
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Description",
                fontFamily = manropeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = travelItem.description.uppercase(Locale.ROOT),
                fontFamily = manropeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Amenities",
                fontFamily = manropeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = amenities,
                fontFamily = manropeFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun LocationAddress(travelItem: TravelItem) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val location = LatLng(travelItem.location.lat, travelItem.location.lng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 16f)
    }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val marker = MarkerOptions()
        .position(location)
        .title(
            toTitleCase(travelItem.name)
        )
        .snippet(toTitleCase(travelItem.name))

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Where you'll be 😄 ",
            fontFamily = manropeFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = properties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                }
            ) {
                Marker(
                    state = MarkerState(
                        position = marker.position
                    ),
                    title = marker.title,
                    snippet = marker.snippet
                ) {

                }
            }
        }
        Text(
            text = travelItem.location.name,
            fontFamily = manropeFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CalendarView(travelItem: TravelItem) {
    val context = LocalContext.current

    val view = LayoutInflater.from(context).inflate(R.layout.calendar_layout, null)
    val calendarView = view.findViewById<MaterialCalendarView>(R.id.calendarView)
    LaunchedEffect(travelItem) {
        val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        val bookedDates = withContext(Dispatchers.IO) {
            travelItem.bookedDates.mapNotNull { dateString ->
                try {
                    org.threeten.bp.LocalDate.parse(dateString, dateFormat)
                } catch (e: Exception) {
                    null
                }
            }
        }

        val bookedDays = bookedDates.mapNotNull { localDate ->
            CalendarDay.from(localDate)
        }
        calendarView.addDecorator(CalendarDecorator(android.graphics.Color.RED, bookedDays))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Availability",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = manropeFamily,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 6.dp)
        )
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            factory = {
                view
            }
        )
    }
}

@Composable
fun HouseRulesDetails(travelItem: TravelItem) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "House rules",
            fontFamily = manropeFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )

        Column {
            Text(
                text = "Check-in: ${travelItem.rules.checkIn} AM",
                fontFamily = manropeFamily,
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
            Text(
                text = "Checkout before: ${travelItem.rules.checkOut} PM",
                fontFamily = manropeFamily,
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
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
                text = "${item.text} ${item.id}",
                fontFamily = manropeFamily,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ReserveCard(travelItem: TravelItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RectangleShape,
        border = BorderStroke(width = 0.2.dp, color = Color.LightGray.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Text(
                    text = "${travelItem.price.currency} ${travelItem.price.amount}",
                    fontFamily = manropeFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = " / night",
                    fontFamily = manropeFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
            Button(
                onClick = {
                },
                colors = ButtonDefaults.buttonColors(containerColor = Yellow200),
                interactionSource = MutableInteractionSource()
            ) {
                Text(
                    text = "Reserve",
                    fontFamily = manropeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
