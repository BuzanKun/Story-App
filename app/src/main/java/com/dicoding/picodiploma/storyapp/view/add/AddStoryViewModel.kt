package com.dicoding.picodiploma.storyapp.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.data.remote.response.UploadResponse
import com.dicoding.picodiploma.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _uploadStatus = MutableLiveData<Result<UploadResponse>>()
    val uploadStatus: LiveData<Result<UploadResponse>> get() = _uploadStatus

    fun uploadStory(multipartBody: MultipartBody.Part, requestBody: RequestBody) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.uploadStory(
                multipartBody,
                requestBody
            )
            _isLoading.value = false
            _uploadStatus.value = result
        }
    }
}