package com.dicoding.picodiploma.storyapp.view.detail

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.storyapp.data.repository.StoryRepository

class DetailViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getStoryById(id: String) = storyRepository.getStoryById(id)
}