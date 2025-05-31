package com.noteapp.navigation

import androidx.navigation.NavHostController

class AppNavigator(private val navController: NavHostController) : Navigator {
    override fun navigateTo(screen: Screens, vararg args: Pair<String, Any?>) {
        val route = if (args.isNotEmpty()) {
            screen.createRouteWithArgs(args.toMap())
        } else {
            screen.route
        }

        navController.navigate(route) {
            launchSingleTop = true // Prevent multiple copies of the same destination
            restoreState = true
        }
    }

    override fun navigateWithClearBackstack(screen: Screens, popUpToScreen: Screens, inclusive: Boolean) {
        navController.navigate(screen.route) {
            launchSingleTop = true
            popUpTo(popUpToScreen.route) {
                this.inclusive = inclusive
            }
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }
}
