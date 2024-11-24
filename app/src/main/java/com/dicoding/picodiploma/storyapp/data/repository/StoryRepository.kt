package com.dicoding.picodiploma.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.data.remote.response.ErrorResponse
import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.data.remote.response.UploadResponse
import com.dicoding.picodiploma.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

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

    suspend fun uploadStory(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody
    ): Result<UploadResponse> {
        return try {
            val response = apiService.uploadStory(multipartBody, requestBody)
            Result.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Result.Error(errorBody.message ?: "Unknown Error Occurred")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error Occurred")
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