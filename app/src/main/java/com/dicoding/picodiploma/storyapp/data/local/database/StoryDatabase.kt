package com.dicoding.picodiploma.storyapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.storyapp.data.local.entity.RemoteKeys
import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem

@Database(entities = [ListStoryItem::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null
        fun getInstance(context: Context): StoryDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "Story.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
    }
}