package com.dicoding.picodiploma.storyapp.view.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.data.Result
import com.dicoding.picodiploma.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.storyapp.utils.getImageUri
import com.dicoding.picodiploma.storyapp.utils.reduceFileImage
import com.dicoding.picodiploma.storyapp.utils.uriToFile
import com.dicoding.picodiploma.storyapp.view.ViewModelFactory
import com.dicoding.picodiploma.storyapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationLat: Float? = null
    private var locationLon: Float? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d(getString(R.string.photo_picker), getString(R.string.selected_image_uri, uri))
                currentImageUri = uri
                showImage()
            } else {
                Log.e(getString(R.string.photo_picker), getString(R.string.no_image_selected))
            }
        }

    private val intentCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                showImage()
            } else {
                currentImageUri = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            edLatitude.keyListener = null
            edLongitude.keyListener = null
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
        supportActionBar?.title = getString(R.string.add_story)
    }

    private fun setupAction() {
        binding?.apply {
            btnGallery.setOnClickListener {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            btnCamera.setOnClickListener {
                currentImageUri = getImageUri(this@AddStoryActivity)
                intentCamera.launch(currentImageUri!!)
            }
            edAddDescription.addTextChangedListener {
                binding?.buttonAdd?.isEnabled = true
            }
            buttonAdd.setOnClickListener {
                uploadStory(locationLat, locationLon)
                binding?.buttonAdd?.isEnabled = false
                binding?.btnGallery?.isEnabled = false
                binding?.btnCamera?.isEnabled = false
            }
            switchLocation.setOnCheckedChangeListener { _, isChecked: Boolean ->
                if (isChecked) {
                    getMyCurrentLocation()
                } else {
                    tilLatitude.isEnabled = false
                    tilLongitude.isEnabled = false
                    locationLon = null
                    locationLat = null
                    binding.apply {
                        edLatitude.text = null
                        edLongitude.text = null
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyCurrentLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyCurrentLocation()
                }

                else -> {
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    locationLat = location.latitude.toFloat()
                    locationLon = location.longitude.toFloat()
                    binding?.apply {
                        tilLatitude.isEnabled = true
                        tilLongitude.isEnabled = true
                        edLatitude.text = Editable.Factory.getInstance().newEditable("$locationLat")
                        edLongitude.text =
                            Editable.Factory.getInstance().newEditable("$locationLon")
                    }
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private fun uploadStory(lat: Float? = null, lon: Float? = null) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d(getString(R.string.image_file), getString(R.string.showimage, imageFile.path))
            val description = binding?.edAddDescription?.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            viewModel.uploadStory(multipartBody, requestBody, lat, lon)
        } ?: showImageNull(getString(R.string.image_null_error))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding?.ivStoryImage?.setImageURI(null)
            binding?.ivStoryImage?.setImageURI(it)
        }
    }

    private fun showImageNull(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.upload_error))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .create()
            .show()
    }

    private fun setupObservers() {
        viewModel.uploadStatus.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.upload_success))
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            navigateToMainActivity()
                        }
                        .setOnDismissListener {
                            navigateToMainActivity()
                        }
                        .create()
                        .show()
                }

                is Result.Error -> {
                    binding?.buttonAdd?.isEnabled = true
                    binding?.btnGallery?.isEnabled = true
                    binding?.btnCamera?.isEnabled = true
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.failed))
                        .setMessage(result.error)
                        .setPositiveButton(getString(R.string.retry)) { _, _ ->
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

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}