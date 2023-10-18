package com.grayseal.travelhubcompose.data.model

data class EmergencyBooking(
    val bookable: Boolean,
    val description: String,
    val discountPercentage: Int,
    val selfCheckin: Boolean
)