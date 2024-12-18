package com.dicoding.picodiploma.storyapp.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private var story: ListStoryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("story", ListStoryItem::class.java)
        } else {
            intent.getParcelableExtra("story")
        }

        setupView()
        displayStoryDetails()
        setupAnimation()
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
        supportActionBar?.title = getString(R.string.story_detail)
    }

    private fun displayStoryDetails() {
        story?.let {
            binding.apply {
                Glide.with(this@DetailActivity)
                    .load(it.photoUrl)
                    .into(ivDetailPhoto)
                tvDetailName.setText(it.name)
                tvDetailDescription.setText(it.description)
                edLatitude.setText(it.lat.toString())
                edLongitude.setText(it.lon.toString())
            }
        }
    }

    private fun setupAnimation() {
        val image = ObjectAnimator.ofFloat(binding.ivDetailPhoto, View.ALPHA, 1f).setDuration(150)
        val nameTitle =
            ObjectAnimator.ofFloat(binding.tvDetailNameTitle, View.ALPHA, 1f).setDuration(150)
        val descriptionTitle =
            ObjectAnimator.ofFloat(binding.tvDetailDescriptionTitle, View.ALPHA, 1f)
                .setDuration(150)
        val nameLayout =
            ObjectAnimator.ofFloat(binding.tilDetailName, View.ALPHA, 1f).setDuration(150)
        val descriptionLayout =
            ObjectAnimator.ofFloat(binding.tilDetailDescription, View.ALPHA, 1f).setDuration(150)
        val name = ObjectAnimator.ofFloat(binding.tvDetailName, View.ALPHA, 1f).setDuration(150)
        val description =
            ObjectAnimator.ofFloat(binding.tvDetailDescription, View.ALPHA, 1f).setDuration(150)

        AnimatorSet().apply {
            playSequentially(
                image,
                nameTitle,
                nameLayout,
                descriptionTitle,
                descriptionLayout,
                name,
                description
            )
            startDelay = 150
        }.start()
    }
}