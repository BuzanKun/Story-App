package com.dicoding.picodiploma.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyapp.data.repository.StoryRepository
import com.dicoding.picodiploma.storyapp.data.repository.UserRepository
import com.dicoding.picodiploma.storyapp.di.Injection
import com.dicoding.picodiploma.storyapp.view.add.AddStoryViewModel
import com.dicoding.picodiploma.storyapp.view.login.LoginViewModel
import com.dicoding.picodiploma.storyapp.view.main.MainViewModel
import com.dicoding.picodiploma.storyapp.view.signup.SignUpViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(
            context: Context
        ): ViewModelFactory {
            val (userRepository, storyRepository) = Injection.provideRepository(context)
            return ViewModelFactory(
                userRepository, storyRepository
            )
        }
    }
}