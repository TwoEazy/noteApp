package com.noteapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noteapp.di.AppContainer
import com.noteapp.ui.screen.AllNotesScreen
import com.noteapp.ui.screen.CreateNoteScreen
import com.noteapp.ui.screen.LoginScreen
import com.noteapp.ui.screen.RegisterScreen
import com.noteapp.viewmodel.AllNotesViewModel
import com.noteapp.viewmodel.AllNotesViewModelFactory
import com.noteapp.viewmodel.CreateNoteViewModel


@Composable
fun Navigation(
    appContainer: AppContainer,
    startDestination: String
) {
    val navController = rememberNavController()
    val navigator = AppNavigator(navController)

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screens.Login.route) {
            LoginScreen(
                viewModel = appContainer.loginViewModel,
                onLoginSuccess = {
                    navigator.navigateWithClearBackstack(
                        screen = Screens.AllNotes,
                        popUpToScreen = Screens.Login,
                        inclusive = true
                    )
                },
                onSignUpClick = {
                    navigator.navigateTo(Screens.Register)
                }
            )
        }

        composable(Screens.Register.route) {
            RegisterScreen(
                viewModel = appContainer.registerViewModel,
                navigator = navigator,
                onRegisterSuccess = {
                    navigator.navigateWithClearBackstack(
                        screen = Screens.Login,
                        popUpToScreen = Screens.Register,
                        inclusive = true
                    )
                }
            )
        }

        composable(Screens.AllNotes.route) {
            val allNotesViewModel: AllNotesViewModel = viewModel(
                factory = appContainer.allNotesViewModelFactory // Use the new factory
            )

            AllNotesScreen(
                viewModel = allNotesViewModel,
                navigator = navigator,
                onLogout = {
                    // Navigate to login and clear the entire back stack
                    navigator.navigateWithClearBackstack(
                        screen = Screens.Login,
                        popUpToScreen = Screens.AllNotes,
                        inclusive = true
                    )
                }
            )
        }

        composable(
            route = Screens.CreateNote.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0

            val createNoteViewModel: CreateNoteViewModel = viewModel(
                factory = appContainer.createNoteViewModelFactory
            )

            CreateNoteScreen(
                viewModel = createNoteViewModel,
                navigator = navigator,
                noteId = noteId
            )
        }
    }
}
