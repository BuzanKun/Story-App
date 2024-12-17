package com.dicoding.picodiploma.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.picodiploma.storyapp.data.local.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.repository.StoryRepository
import com.dicoding.picodiploma.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    storyRepository: StoryRepository
) : ViewModel() {

    val stories = storyRepository.getStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}