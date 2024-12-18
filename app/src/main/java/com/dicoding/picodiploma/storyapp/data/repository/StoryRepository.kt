package com.dicoding.picodiploma.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.data.StoryRemoteMediator
import com.dicoding.picodiploma.storyapp.data.local.database.StoryDatabase
import com.dicoding.picodiploma.storyapp.data.remote.response.ErrorResponse
import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.data.remote.response.UploadResponse
import com.dicoding.picodiploma.storyapp.data.remote.retrofit.ApiService
import com.dicoding.picodiploma.storyapp.utils.wrapEspressoIdlingResource
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStoriesWithRemoteMediator(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 4,
                initialLoadSize = 8
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            val storiesData = response.listStory
            emit(Result.Success(storiesData))
        } catch (e: Exception) {
            Log.e("StoryRepository", "getStoriesWithLocation: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun uploadStory(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody,
        lat: Float?,
        lon: Float?
    ): Result<UploadResponse> {
        wrapEspressoIdlingResource {
            return try {
                val response = apiService.uploadStory(multipartBody, requestBody, lat, lon)
                Result.Success(response)
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                Result.Error(errorBody.message ?: "Unknown Error Occurred")
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown Error Occurred")
            }
        }
    }

    companion object {
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService
        ): StoryRepository = StoryRepository(storyDatabase, apiService)
    }
}