package com.example.android.gdgfinder

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.gdgfinder.search.GdgListAdapter
import com.example.android.gdgfinder.search.RegionListAdapter
import com.example.android.gdgfinder.network.GdgChapter

/**
 * When there is no Mars property data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GdgChapter>?) {
    val adapter = recyclerView.adapter as GdgListAdapter
    adapter.submitList(data) {
        recyclerView.scrollToPosition(0)
    }
}
@BindingAdapter("regionData")
fun bindRegionsToRecyclerView(recyclerView: RecyclerView, data: List<String>?) {
    val adapter = recyclerView.adapter as RegionListAdapter
    adapter.submitList(data)
}
