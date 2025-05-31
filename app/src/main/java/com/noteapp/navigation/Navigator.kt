package com.noteapp.navigation

interface Navigator {
    fun navigateTo(screen: Screens, vararg args: Pair<String, Any?>)
    fun navigateWithClearBackstack(screen: Screens, popUpToScreen: Screens, inclusive: Boolean)
    fun navigateUp()
}
