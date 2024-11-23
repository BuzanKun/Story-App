package com.dicoding.picodiploma.storyapp.data.local.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)