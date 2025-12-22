package com.example.mindspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindspace.navigation.Screen
import com.example.mindspace.ui.screens.AddNoteScreen
import com.example.mindspace.ui.screens.NoteListScreen
import com.example.mindspace.ui.theme.MindSpaceTheme

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
    
    NavHost(
        navController = navController,
        startDestination = Screen.NoteList.route
    ){
        composable(Screen.NoteList.route){
            NoteListScreen(
                onAddNote = { navController.navigate(Screen.AddNote.route) },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                }
            )
        }
        composable(Screen.AddNote.route){
            AddNoteScreen(
                onNavigateBack = { navController.popBackStack() }
            )

        }
    }
    
}



@Composable
fun Title(text: String) {
    Text(text = text)  // Declarative: DESCRIBE what it should be
}
