package com.example.mindspace.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mindspace.NoteViewModel
import com.example.mindspace.ui.components.ReminderBottomSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit
) {
    // Load the note initially when the composable enters the composition
    LaunchedEffect(key1 = noteId) {
        viewModel.loadNoteById(noteId)
    }

    val note by viewModel.selectedNote.collectAsState()

    // State to toggle between view and edit modes
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showReminderSheet by remember { mutableStateOf(false) }

    // State for the text fields in edit mode
    var editTitle by remember { mutableStateOf("") }
    var editContent by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("General") }


    // Update the edit fields whenever the selected note changes.
    LaunchedEffect(key1 = note) {
        note?.let {
            editTitle = it.title
            editContent = it.content
            editCategory = it.category
        }
    }

    val hasUnsavedChanges = remember(editTitle, editContent, editCategory, note) {
        note?.let {
            editTitle != it.title || editContent != it.content || editCategory != it.category
        } ?: false
    }

    BackHandler(enabled = isEditing && hasUnsavedChanges) {
        showUnsavedDialog = true
    }

    val currentNote = note

    if (currentNote == null) {
        // Show a loading spinner if the note is not yet loaded
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(if (isEditing) "Edit Note" else currentNote.title)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isEditing && hasUnsavedChanges) {
                                showUnsavedDialog = true
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                        }
                    },
                    actions = {
                        if (isEditing) {
                            // When editing, show Save button
                            IconButton(onClick = {
                                viewModel.updateNote(
                                    currentNote.copy(
                                        title = editTitle,
                                        content = editContent,
                                        category = editCategory,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                                isEditing = false // Exit edit mode
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        } else {
                            // When viewing, show Edit and Reminder buttons
                            IconButton(onClick = { showReminderSheet = true }) {
                                Icon(
                                    if (note?.hasReminder == true) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Set reminder",
                                    tint = if (note?.hasReminder == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                        // Always show Delete button
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
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
                if (isEditing) {
                    // --- EDITING VIEW ---
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    CategoryDropdown(
                        selectedCategory = editCategory,
                        onCategorySelected = { editCategory = it },
                        modifier = Modifier.fillMaxWidth()

                    )

                    OutlinedTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
                        label = { Text("Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), // Take up remaining vertical space
                        minLines = 10
                    )
                } else {
                    // --- DISPLAY VIEW ---
                    Text(
                        text = formatDetailTimestamp(currentNote.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (currentNote.category.isNotBlank()) {
                        AssistChip(
                            onClick = { /* no-op */ },
                            label = { Text(currentNote.category) }
                        )
                    }
                    Divider()
                    Text(
                        text = currentNote.content,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f) // Take up remaining vertical space
                    )
                }
            }
        }

        // Reminder bottom sheet
        if (showReminderSheet) {
            ReminderBottomSheet(
                currentReminderTime = note?.reminderTime,
                onSetReminder = {
                    viewModel.setReminder(noteId, it)
                    showReminderSheet = false
                },
                onCancelReminder = {
                    viewModel.cancelReminder(noteId)
                    showReminderSheet = false
                },
                onDismiss = { showReminderSheet = false }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Move to Trash?") },
                text = { Text("Are you sure you want to move this note to the trash?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteNote(currentNote.id)
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    ) {
                        Text("Move to Trash", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showUnsavedDialog) {
            AlertDialog(
                onDismissRequest = { showUnsavedDialog = false },
                title = { Text("Unsaved Changes") },
                text = { Text("You have unsaved changes. Do you want to discard them and go back?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showUnsavedDialog = false
                            isEditing = false
                            // Reset changes to original
                            note?.let {
                                editTitle = it.title
                                editContent = it.content
                                editCategory = it.category
                            }
                            onNavigateBack()
                        }
                    ) {
                        Text("Discard & Go Back")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUnsavedDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

}

private fun formatDetailTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
