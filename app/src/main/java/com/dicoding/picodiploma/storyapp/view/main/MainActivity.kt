package com.dicoding.picodiploma.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.storyapp.view.ViewModelFactory
import com.dicoding.picodiploma.storyapp.view.add.AddStoryActivity
import com.dicoding.picodiploma.storyapp.view.auth.AuthActivity
import com.dicoding.picodiploma.storyapp.view.auth.AuthViewModel
import com.dicoding.picodiploma.storyapp.view.storymaps.StoryMapsActivity
import com.dicoding.picodiploma.storyapp.widget.StoryWidget
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val factory by lazy {
        ViewModelFactory.getInstance(this)
    }
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }
    private val authViewModel: AuthViewModel by viewModels {
        factory
    }
    private val storyAdapter = StoryAdapter()

    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        binding.rvStoryList.layoutManager = LinearLayoutManager(this)

        setupView()
        loadData()
        setupAction()
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
    }

    private fun loadData() {
        mainViewModel.storiesWithRemoteMediator.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }

        storyAdapter.addLoadStateListener { loadState ->
            Log.d("LoadState", "Current state: $loadState")
            when (loadState.refresh) { // For initial load
                is LoadState.Loading -> {
                    Log.d(" Loading", "Loading")
                    binding.progressBar.visibility = View.VISIBLE
                    var progress = 0
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            progress += 10
                            if (progress <= 100) {
                                binding.progressBar.progress = progress
                                handler.postDelayed(this, 100) // Update every 300ms
                            }
                        }
                    }, 100)
                    errorSnackbar?.dismiss()
                }

                is LoadState.NotLoading -> {
                    Log.d("SUCCESS", "Success")
                    binding.progressBar.visibility = View.GONE
                    errorSnackbar?.dismiss()
                }

                is LoadState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    val error = (loadState.refresh as LoadState.Error).error
                    if (errorSnackbar == null || errorSnackbar!!.isShown) {
                        errorSnackbar = Snackbar.make(
                            binding.root,
                            getString(R.string.error_occurred, error.localizedMessage),
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(getString(R.string.retry)) {
                            storyAdapter.retry()
                        }
                        errorSnackbar?.show()
                    }
                }
            }
        }

        if (binding.rvStoryList.adapter == null) {
            binding.rvStoryList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = storyAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { storyAdapter.retry() }
                )
            }
        }
    }

    private fun setupAction() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            storyAdapter.refresh()
            loadData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_map -> {
                startActivity(Intent(this, StoryMapsActivity::class.java))
            }

            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.action_logout -> {
                authViewModel.logout()
                StoryWidget.notifyDataSetChanged(this)
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}