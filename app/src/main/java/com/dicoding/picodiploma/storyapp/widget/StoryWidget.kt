package com.dicoding.picodiploma.storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.dicoding.picodiploma.storyapp.R


class StoryWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "ACTION_WIDGET_BUTTON") {
            notifyDataSetChanged(context)
        }
    }

    companion object {
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StoryWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val views = RemoteViews(context.packageName, R.layout.story_widget)
            views.setRemoteAdapter(R.id.list_view, intent)
            views.setEmptyView(R.id.list_view, R.id.empty_view)

            val buttonIntent = Intent(context, StoryWidget::class.java).apply {
                action = "ACTION_WIDGET_BUTTON"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                buttonIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.button_widget, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun notifyDataSetChanged(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, StoryWidget::class.java)
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view)
            val intent = Intent(context, StoryWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }
}