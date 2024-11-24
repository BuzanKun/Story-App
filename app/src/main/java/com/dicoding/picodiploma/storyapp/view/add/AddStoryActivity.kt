package com.dicoding.picodiploma.storyapp.view.add

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.storyapp.utils.reduceFileImage
import com.dicoding.picodiploma.storyapp.utils.uriToFile
import com.dicoding.picodiploma.storyapp.view.ViewModelFactory
import com.dicoding.picodiploma.storyapp.view.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding

    private val factory by lazy {
        ViewModelFactory.getInstance(this)
    }
    private val viewModel: AddStoryViewModel by viewModels {
        factory
    }

    private var currentImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("Photo Picker", "Selected Image Uri: $uri")
                currentImageUri = uri
                currentImageUri?.let {
                    binding?.ivStoryImage?.setImageURI(null)
                    binding?.ivStoryImage?.setImageURI(it)
                }
            } else {
                Log.e("Photo Picker", "No Image Selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupAction()
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
        supportActionBar?.title = "Add Story"
    }

    private fun setupAction() {
        binding?.btnGallery?.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding?.btnUpload?.setOnClickListener {
            uploadStory()
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding?.etDescription?.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.uploadStory(multipartBody, requestBody)
        } ?: showImageNull(getString(R.string.image_null_error))
    }

    private fun showImageNull(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Upload Error")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
            }
            .create()
            .show()
    }

    private fun setupObservers() {
        viewModel.uploadStatus.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    AlertDialog.Builder(this)
                        .setTitle("Success")
                        .setMessage("Upload Success")
                        .setPositiveButton("OK") { _, _ ->
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        .create()
                        .show()
                }

                is Result.Error -> {
                    AlertDialog.Builder(this)
                        .setTitle("Failed")
                        .setMessage(result.error)
                        .setPositiveButton("Retry") { _, _ ->
                        }
                        .create()
                        .show()
                }

                Result.Loading -> TODO()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding?.progressBar!!.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}