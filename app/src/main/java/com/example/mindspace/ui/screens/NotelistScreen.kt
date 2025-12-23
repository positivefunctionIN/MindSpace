package com.example.mindspace.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mindspace.NoteViewModel
import com.example.mindspace.data.local.Note
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NoteViewModel,  // Add ViewModel parameter!
    onAddNote: () -> Unit,
    onNoteClick: (Int) -> Unit
) {
    // Collect notes from ViewModel's Flow
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MindSpace") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note"
                )
            }
        }
    ) { paddingValues ->

        if (notes.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("📝", style = MaterialTheme.typography.displayLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Welcome to MindSpace", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your personal space for thoughts", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Click the + button to create your first note", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            // List of notes
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = notes,
                    key = { note -> note.id }
                ) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp
            Text(
                text = formatTimestamp(note.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
