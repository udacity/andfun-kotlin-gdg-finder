package com.example.android.gdgfinder.search

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.gdgfinder.databinding.FragmentGdgListBinding
import com.example.android.gdgfinder.search.GdgListViewModel

import com.google.android.material.snackbar.Snackbar

class GdgListFragment : Fragment() {

    private val viewModel: GdgListViewModel by lazy {
        ViewModelProviders.of(this).get(GdgListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentGdgListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        val adapter = GdgListAdapter(GdgClickListener {
            //this.findNavController().navigate(GdgListFragmentDirections.actionShowDetail(it))

            Snackbar.make(
                activity!!.findViewById(android.R.id.content),
                it.name,
                Snackbar.LENGTH_SHORT // How long to display the message.
            ).show()
        })
        val adapterRegion = RegionListAdapter(RegionClickListener {
                region ->  viewModel.onRegionSelected(region)
            Snackbar.make(
                activity!!.findViewById(android.R.id.content),
                region,
                Snackbar.LENGTH_SHORT // How long to display the message.
            ).show()
        })
//        // Sets the adapter of the photosGrid RecyclerView
        binding.gdgChapterList.adapter = adapter
        binding.regionsList.adapter = adapterRegion

        setHasOptionsMenu(true)
        return binding.root
    }

}


