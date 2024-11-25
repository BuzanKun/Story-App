package com.dicoding.picodiploma.storyapp.data.repository

import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.data.local.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.local.pref.UserPreference
import com.dicoding.picodiploma.storyapp.data.remote.response.ErrorResponse
import com.dicoding.picodiploma.storyapp.data.remote.response.RegisterResponse
import com.dicoding.picodiploma.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            Result.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Result.Error(errorBody.message ?: "Unknown Error Occurred")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error Occurred")
        }
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Result<UserModel> {
        return try {
            val response = apiService.login(email, password)
            if (response.error == true) {
                Result.Error(response.message ?: "Login failed")
            } else {
                val loginResult = response.loginResult
                if (loginResult != null) {
                    val user = UserModel(
                        email = email,
                        token = loginResult.token ?: "",
                        isLogin = true
                    )
                    userPreference.saveSession(user)
                    Result.Success(user)
                } else {
                    Result.Error("Invalid login response")
                }
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Result.Error(errorBody.message ?: "Unknown Error Occurred")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error Occurred")
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(
            apiService: ApiService, userPreference: UserPreference
        ): UserRepository = UserRepository(apiService, userPreference)
    }
}