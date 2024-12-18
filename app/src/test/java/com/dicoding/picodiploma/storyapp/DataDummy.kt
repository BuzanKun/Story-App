package com.dicoding.picodiploma.storyapp

import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "name + $i",
                description = "story + $i"
            )
            items.add(story)
        }
        return items
    }
}