package com.dicoding.picodiploma.storyapp.view.storymaps

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.data.repository.StoryRepository

class StoryMapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}