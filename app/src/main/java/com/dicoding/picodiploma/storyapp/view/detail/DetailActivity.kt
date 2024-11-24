package com.dicoding.picodiploma.storyapp.view.detail

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.storyapp.view.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val factory by lazy {
        ViewModelFactory.getInstance(this)
    }
    private val viewModel: DetailViewModel by viewModels {
        factory
    }
    private var storyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        storyId = intent.getStringExtra("id")
        setupObservers()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.title = "Story Detail"
    }

    private fun setupObservers() {
        storyId?.let {
            viewModel.getStoryById(it).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            Log.d(" Loading", "Loading")
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            Log.d("SUCCESS", "Success")
                            binding.progressBar.visibility = View.GONE
                            val storyData = result.data
                            Log.d(
                                "Story Data Fields",
                                "Photo: ${storyData.photoUrl}, Name: ${storyData.name}, Description: ${storyData.description}, CreatedAt: ${storyData.createdAt}"
                            )
                            Glide.with(this@DetailActivity)
                                .load(storyData.photoUrl)
                                .into(binding.ivStoryImage)
                            binding.tvStoryName.text = storyData.name
                            binding.tvStoryDescription.text = storyData.description
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                "Error Occurred: ${result.error}",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Dismiss") {
                            }.show()
                        }
                    }
                }
            }
        }
    }
}