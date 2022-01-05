package com.hamurcuabi.imdbapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hamurcuabi.imdbapp.data.network.model.common.MovieOverview
import com.hamurcuabi.imdbapp.databinding.ItemMovieOverviewBinding

class NowPlayingMovieAdapter(
    private val onItemClicked: (MovieOverview) -> Unit
) : ListAdapter<MovieOverview, NowPlayingMovieAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMovieOverviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) {
            onItemClicked(this.getItem(it))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ViewHolder(private val binding: ItemMovieOverviewBinding, onItemClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onItemClicked(absoluteAdapterPosition) }
        }

        fun bind(model: MovieOverview) {
            binding.item = model
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<MovieOverview>() {
        override fun areItemsTheSame(
            oldItem: MovieOverview,
            newItem: MovieOverview
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MovieOverview,
            newItem: MovieOverview
        ) =
            oldItem == newItem
    }
}