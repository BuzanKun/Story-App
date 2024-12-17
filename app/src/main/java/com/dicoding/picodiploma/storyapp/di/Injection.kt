package com.dicoding.picodiploma.storyapp.di

import android.content.Context
import com.dicoding.picodiploma.storyapp.data.local.database.StoryDatabase
import com.dicoding.picodiploma.storyapp.data.local.pref.UserPreference
import com.dicoding.picodiploma.storyapp.data.local.pref.dataStore
import com.dicoding.picodiploma.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.picodiploma.storyapp.data.remote.retrofit.ApiService
import com.dicoding.picodiploma.storyapp.data.repository.StoryRepository
import com.dicoding.picodiploma.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Pair<UserRepository, StoryRepository> {
        val userPref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getInstance(context)

        val userRepository = UserRepository.getInstance(apiService, userPref)
        val storyRepository = StoryRepository.getInstance(storyDatabase, apiService)
        return Pair(userRepository, storyRepository)
    }

    fun provideApiService(context: Context): ApiService {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        return ApiConfig.getApiService(user.token)
    }
}