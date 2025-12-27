package com.example.mindspace.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.mindspace.workers.ReminderWorker
import androidx.work.*
import java.util.concurrent.TimeUnit

object ReminderManager {
    fun scheduleReminder(
        context: Context, noteId: Int, reminderTime: Long) {
        val currentTine = System.currentTimeMillis()
        val delay = reminderTime - currentTime

        if(delay <= 0){
            return
            //time has already passed
        }

        val inputData = workDataOf("noteId" to noteId)
        val reminderWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_$noteId")
            .build()

        WorkManager.getInstance(context)
            .enqueue(
                "reminder_$noteId",
                ExistingWorkPolicy.REPLACE,
                reminderWork
            )
    }

    fun cancelReminder(context: Context, noteId: Int){
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("reminder_$noteId")
    }
}