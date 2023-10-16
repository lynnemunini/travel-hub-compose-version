package com.grayseal.travelhubcompose.navigation

enum class TravelHubScreens {
    SignInScreen,
    SignUpScreen,
    HomeScreen,
    DetailsScreen;

    companion object {
        fun fromRoute(route: String): TravelHubScreens = when (route.substringBefore("/")) {
            SignInScreen.name -> SignInScreen
            SignUpScreen.name -> SignUpScreen
            HomeScreen.name -> HomeScreen
            DetailsScreen.name -> DetailsScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}