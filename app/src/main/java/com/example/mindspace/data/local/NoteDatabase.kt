package com.example.mindspace.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Note::class],
    version = 3, // Incremented for the reminder feature
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    companion object {
       @Volatile
       private var INSTANCE: NoteDatabase? = null
       fun getDatabase(context: Context): NoteDatabase {
           return INSTANCE ?: synchronized(this) {
               val instance = Room.databaseBuilder(
                   context.applicationContext,
                   NoteDatabase::class.java,
                   "note_database"
               )
               .fallbackToDestructiveMigration()
               .build()
               INSTANCE = instance
               instance
           }
       }
    }

}