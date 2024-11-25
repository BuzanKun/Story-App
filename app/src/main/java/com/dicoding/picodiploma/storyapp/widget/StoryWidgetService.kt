package com.dicoding.picodiploma.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService

class StoryWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        println("onGetViewFactory called") // Debugging
        return StoryRemoteViewsFactory(applicationContext)
    }
}