package com.dicoding.picodiploma.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.data.UserRepository
import com.dicoding.picodiploma.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _registerStatus = MutableLiveData<Result<RegisterResponse>>()
    val registerStatus: LiveData<Result<RegisterResponse>> get() = _registerStatus

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.registerUser(name, email, password)
            _isLoading.value = false
            _registerStatus.value = result // Result.Success or Result.Error
        }
    }
}
