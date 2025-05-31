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
                    navigator.navigateTo(Screens.AllNotes)
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
                    navigator.navigateTo(Screens.Login)
                }
            )
        }

        composable(Screens.AllNotes.route) {
            val allNotesViewModel: AllNotesViewModel = viewModel(
                factory = AllNotesViewModelFactory(appContainer.noteRepository)
            )

            AllNotesScreen(
                viewModel = allNotesViewModel,
                navigator = navigator,
                onLogout = {
                    // Handle logout
                    appContainer.sessionManager.clearSession() // Clear session on logout

                    // Use the new navigation method to clear the back stack
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

            // Use the factory from appContainer that includes SessionManager
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
