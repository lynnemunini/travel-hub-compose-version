package com.grayseal.travelhubcompose.navigation

/**
 * Enum class representing different screens in the Travel Hub app.
 * Includes screens for sign-in, sign-up, home, and details.
 */
enum class TravelHubScreens {
    SignInScreen,
    SignUpScreen,
    HomeScreen,
    DetailsScreen;

    /**
     * Companion object providing utility functions for screen management.
     */
    companion object {
        /**
         * Converts a route string to the corresponding TravelHubScreens enum.
         *
         * @param route The route string to be converted.
         * @return The corresponding TravelHubScreens enum.
         * @throws IllegalArgumentException if the route is not recognized.
         */
        fun fromRoute(route: String): TravelHubScreens = when (route.substringBefore("/")) {
            SignInScreen.name -> SignInScreen
            SignUpScreen.name -> SignUpScreen
            HomeScreen.name -> HomeScreen
            DetailsScreen.name -> DetailsScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}