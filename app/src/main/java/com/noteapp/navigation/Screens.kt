package com.noteapp.navigation

sealed class Screens(val route: String, val title: String) {
    object Login : Screens("login", "Login")
    object Register : Screens("register", "Register")
    object AllNotes : Screens("all_notes", "All Notes")
    object CreateNote : Screens("create_note?noteId={noteId}", "Create Note") {
        override fun createRouteWithArgs(args: Map<String, Any?>): String {
            val noteId = args["noteId"] ?: 0
            return "create_note?noteId=$noteId"
        }
    }

    open fun createRouteWithArgs(args: Map<String, Any?>): String {
        return route
    }
}
