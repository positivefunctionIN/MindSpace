package com.example.mindspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mindspace.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    viewModel: NoteViewModel,  // Add ViewModel parameter!
    onNavigateBack: () -> Unit
) {
    // State for form fields
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    showError = false
                },
                label = { Text("Title") },
                placeholder = { Text("Enter note title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && title.isBlank(),
                supportingText = if (showError && title.isBlank()) {
                    { Text("Title is required") }
                } else null
            )

            // Content input
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    showError = false
                },
                label = { Text("Content") },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 10,
                isError = showError && content.isBlank()
            )

            // Save button
            Button(
                onClick = {
                    if (title.isBlank() && content.isBlank()) {
                        showError = true
                    } else {
                        viewModel.addNote(
                            title = title.ifBlank { "Untitled" },
                            content = content
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Note")
            }
        }
    }
}