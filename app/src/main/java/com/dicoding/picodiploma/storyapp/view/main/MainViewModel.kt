package com.dicoding.picodiploma.storyapp.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.picodiploma.storyapp.data.repository.StoryRepository

class MainViewModel(
    storyRepository: StoryRepository
) : ViewModel() {
    val storiesWithRemoteMediator =
        storyRepository.getStoriesWithRemoteMediator().cachedIn(viewModelScope)
}