package com.dicoding.picodiploma.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.data.remote.retrofit.ApiService

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    fun getStories(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories()
            val storiesData = response.listStory
            emit(Result.Success(storiesData))
        } catch (e: Exception) {
            Log.e("StoryRepository", "getStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null
        fun getInstance(apiService: ApiService): StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(apiService)
            }.also { INSTANCE = it }
    }
}