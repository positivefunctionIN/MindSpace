package com.example.mindspace.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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

        // Migration from version 2 to 3: Adds reminder fields
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE notes ADD COLUMN hasReminder INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE notes ADD COLUMN reminderTime INTEGER"
                )
            }
        }

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "mindspace_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration() // Add safety net for other migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
