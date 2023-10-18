package com.grayseal.travelhubcompose.data.model

data class Location(
    val city: String,
    val country: String,
    val lat: Double,
    val lng: Double,
    val name: String,
    val street: String
)