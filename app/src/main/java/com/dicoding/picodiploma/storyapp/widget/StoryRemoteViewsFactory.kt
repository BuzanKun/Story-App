package com.dicoding.picodiploma.storyapp.widget

import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.data.local.pref.UserPreference
import com.dicoding.picodiploma.storyapp.data.local.pref.dataStore
import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.di.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private val stories = mutableListOf<ListStoryItem>()
    private val storiesImageBitmap = mutableListOf<Bitmap>()
    private val storiesDescription = mutableListOf<String>()
    private val apiService = Injection.provideApiService(context)
    private val userPreference = UserPreference.getInstance(context.dataStore)

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        // Load stories from DataStore
        runBlocking(Dispatchers.IO) {
            val token = userPreference.getSession().first().token
            if (token != "") {
                try {
                    val result = apiService.getStories()
                    stories.addAll(result.listStory)

                    val bitmap = result.listStory.map {
                        Glide.with(context)
                            .asBitmap()
                            .load(it.photoUrl)
                            .transform(RoundedCorners(8))
                            .submit()
                            .get()
                    }

                    val nameString = result.listStory.mapNotNull {
                        it.description
                    }
                    storiesImageBitmap.addAll(bitmap)
                    storiesDescription.addAll(nameString)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                stories.clear()
                storiesImageBitmap.clear()
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.story_widget_item).apply {
            if (position < stories.size) {
                setImageViewBitmap(R.id.iv_widget_photo, storiesImageBitmap[position])
                setTextViewText(R.id.tv_widget_description, storiesDescription[position])
            }
        }

        val story = stories[position]
        println("Binding story at position $position: $story")
        return views
    }

    override fun getCount(): Int = stories.size
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun onDestroy() {
        stories.clear()
        storiesImageBitmap.clear()
    }
}
