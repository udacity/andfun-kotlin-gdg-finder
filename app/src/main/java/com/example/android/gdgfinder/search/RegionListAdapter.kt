package com.example.android.gdgfinder.search


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.gdgfinder.databinding.RegionBinding
import com.example.android.gdgfinder.search.RegionListAdapter.RegionViewHolder

class RegionListAdapter(val clickListener: RegionClickListener) : ListAdapter<FiltersWithSelected, RegionViewHolder>(DiffCallback){

    companion object DiffCallback : DiffUtil.ItemCallback<FiltersWithSelected>() {
        override fun areItemsTheSame(oldItem: FiltersWithSelected, newItem: FiltersWithSelected): Boolean {
            return oldItem.region  === newItem.region
        }

        override fun areContentsTheSame(oldItem: FiltersWithSelected, newItem: FiltersWithSelected): Boolean {
            return oldItem == newItem
        }
    }


    class RegionViewHolder(private var binding: RegionBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: RegionClickListener, region: FiltersWithSelected) {
            binding.region = region.region
            binding.selected = region.selected
            binding.clickListener = listener
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements efficently
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RegionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RegionBinding.inflate(layoutInflater, parent, false)
                return RegionViewHolder(binding)
            }
        }
    }

    /**
     * Part of the RecyclerView adapter, called when RecyclerView needs a new [RegionViewHolder].
     *
     * A ViewHolder holds a view for the [RecyclerView] as well as providing additional information
     * to the RecyclerView such as where on the screen it was last drawn during scrolling.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        return RegionViewHolder.from(parent)

    }

    /**
     * Part of the RecyclerView adapter, called when RecyclerView needs to show an item.
     *
     * The ViewHolder passed may be recycled, so make sure that this sets any properties that
     * may have been set previously.
     */
    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }
}

class RegionClickListener(val clickListener: (region: String) -> Unit) {
    fun onClick(region: String) = clickListener(region)
}