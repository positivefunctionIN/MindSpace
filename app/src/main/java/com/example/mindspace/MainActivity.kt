package com.example.mindspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindspace.data.local.NoteDatabase
import com.example.mindspace.data.repository.NoteRepository
import com.example.mindspace.navigation.Screen
import com.example.mindspace.ui.screens.AddNoteScreen
import com.example.mindspace.ui.screens.NoteDetailScreen
import com.example.mindspace.ui.screens.NoteListScreen
import com.example.mindspace.ui.theme.MindSpaceTheme
import com.example.mindspace.ui.trash.TrashScreen
import com.example.mindspace.ui.trash.TrashViewModel
import com.example.mindspace.ui.trash.TrashViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindSpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MindSpaceApp()
                }
            }
        }
    }
}

@Composable
fun MindSpaceApp() {
    val navController: NavHostController = rememberNavController()

    // Create a single instance of the repository
    val noteDao = NoteDatabase.getDatabase(navController.context).noteDao()
    val noteRepository = NoteRepository(noteDao)

    val noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(noteRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.NoteList.route
    ) {
        composable(Screen.NoteList.route) {
            NoteListScreen(
                viewModel = noteViewModel,
                onAddNote = { navController.navigate(Screen.AddNote.route) },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onGoToTrash = { navController.navigate(Screen.Trash.route) }
            )
        }

        composable(Screen.AddNote.route) {
            AddNoteScreen(
                viewModel = noteViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            NoteDetailScreen(
                noteId = noteId,
                viewModel = noteViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Trash.route) {
            val trashViewModel: TrashViewModel = viewModel(
                factory = TrashViewModelFactory(noteRepository)
            )
            TrashScreen(
                viewModel = trashViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
