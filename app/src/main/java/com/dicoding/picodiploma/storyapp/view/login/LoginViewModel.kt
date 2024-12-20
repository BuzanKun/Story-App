package com.dicoding.picodiploma.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.data.local.pref.UserModel
import com.dicoding.picodiploma.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _loginStatus = MutableLiveData<Result<UserModel>>()
    val loginStatus: LiveData<Result<UserModel>> get() = _loginStatus

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _isLoading.value = false
            _loginStatus.value = result
        }
    }
}