package com.example.mindspace.navigation

sealed class Screen(val route: String) {
    object NoteList : Screen("note_list")
    object AddNote : Screen("add_note")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
    object Analytics : Screen("analytics")

}