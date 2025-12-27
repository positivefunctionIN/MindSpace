package com.example.mindspace.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mindspace.data.local.NoteDatabase
import com.example.mindspace.utils.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val noteId = inputData.getInt("noteId", -1)
            if (noteId == -1) return@withContext Result.failure()

            //GET note from DATABASE
            val database = NoteDatabase.getDatabase(applicationContext)
            val note = database.noteDao().getNoteById(noteId)

            //SHOW NOTIFICATION
            note?.let {
                NotificationHelper.showReminderNotification(
                    context = applicationContext,
                    noteId = it.id,
                    title = it.title,
                    content = it.content
                )

                // Clear reminder from note
                database.noteDao().updateReminder(
                    noteId = it.id,
                    hasReminder = false,
                    reminderTime = null,
                    updatedAt = System.currentTimeMillis()
                )
            }

            Result.success()
        }
        catch (e: Exception) {
            Result.failure()
        }
    }
}
