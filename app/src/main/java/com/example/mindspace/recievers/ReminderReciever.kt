package com.example.mindspace.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mindspace.data.local.NoteDatabase
import com.example.mindspace.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra("noteId", -1)
        if (noteId == -1) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = NoteDatabase.getDatabase(context)
                val note = database.noteDao().getNoteById(noteId)

                note?.let {
                    NotificationHelper.showReminderNotification(
                        context = context,
                        noteId = it.id,
                        title = it.title.ifEmpty { "MindSpace Reminder" },
                        content = it.content.ifEmpty { "You have a reminder!" }
                    )

                    // Correctly call updateReminder with all required parameters
                    database.noteDao().updateReminder(
                        noteId = it.id,
                        hasReminder = false,
                        reminderTime = null,
                        updatedAt = System.currentTimeMillis() // This was missing
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
