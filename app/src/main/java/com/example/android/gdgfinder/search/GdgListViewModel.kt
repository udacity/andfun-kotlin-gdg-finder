package com.example.android.gdgfinder.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.gdgfinder.network.GdgChapter
import com.example.android.gdgfinder.network.GdgApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class GdgListViewModel: ViewModel(){


    //private val _filteredList = MutableLiveData<List<GdgChapter>>()
    private val _gdgList = MutableLiveData<List<GdgChapter>>()
    private val _regionList = MutableLiveData<List<String>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val gdgList: LiveData< List<GdgChapter>>
        get() = _gdgList

    val regionList: LiveData<List<String>>
        get() = _regionList

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
       // _filteredList.value= listOf()
        getGdgChapterList()
    }

    private fun getGdgChapterList() {
        coroutineScope.launch {
            try {
                // this will run on a thread managed by Retrofit
                val result = GdgApi.retrofitService.getChapters().await()
                _regionList.value = result.filters.regions
                _gdgList.value = result.chapters//.map { it.region to listOf(it) }.toMap()

               // filterList(result.filters.regions)

            } catch (e: Exception) {
                _gdgList.value = listOf()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onRegionSelected(region: String) {

    }
}

