package com.example.mindspace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,

    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,

    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
