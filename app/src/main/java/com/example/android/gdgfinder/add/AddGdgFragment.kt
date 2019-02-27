package com.example.android.gdgfinder.add

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.android.gdgfinder.R

class AddGdgFragment : Fragment() {

    companion object {
        fun newInstance() = AddGdgFragment()
    }

    private lateinit var viewModel: AddGdgViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_gdg_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddGdgViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
