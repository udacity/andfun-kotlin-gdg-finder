package com.example.android.gdgfinder.search

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.gdgfinder.network.GdgApi
import com.example.android.gdgfinder.network.GdgChapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException


class GdgListViewModel: ViewModel() {

    private val repository = GdgChapterRepository(GdgApi.retrofitService)

    private var currentLocation: Location? = null
    private var currentFilter: String? = null

    private var currentJob: Job? = null

    //private val _filteredList = MutableLiveData<List<GdgChapter>>()
    private val _gdgList = MutableLiveData<List<GdgChapter>>()
    private val _regionList = MutableLiveData<List<String>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val gdgList: LiveData< List<GdgChapter>>
        get() = _gdgList

    val regionList: LiveData<List<String>>
        get() = _regionList

    init {
        // process the initial filter
        onQueryChanged()
    }

    private fun onQueryChanged() {
        currentJob?.cancel() // if a previous query is running cancel it before starting another
        currentJob = viewModelScope.launch {
            try {
                // this will run on a thread managed by Retrofit
                val updatedValues = repository.getGdgInformationByLocation(currentFilter, currentLocation)
                _regionList.value = updatedValues.filters
                _gdgList.value = updatedValues.chapters
            } catch (e: IOException) {
                _gdgList.value = listOf()
            }
        }
    }

    fun onLocationUpdated(location: Location) {
        currentLocation = location
        onQueryChanged()
    }

    fun onFilterChanged(filter: String) {
        if (currentFilter == filter) {
            currentFilter = null
        } else {
            currentFilter = filter
        }
        onQueryChanged()
    }
}

