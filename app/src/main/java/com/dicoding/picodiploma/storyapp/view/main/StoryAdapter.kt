package com.dicoding.picodiploma.storyapp.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.databinding.StoryRowBinding
import com.dicoding.picodiploma.storyapp.view.detail.DetailActivity

class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: StoryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivStoryImage)
            binding.tvStoryName.text =
                binding.root.context.getString(R.string.story_username, story.name)
            binding.tvStoryDescription.text =
                binding.root.context.getString(R.string.story_description, story.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("id", story.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}