package com.example.android.gdgfinder.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;

class HomeViewModel : ViewModel() {
    private val _navigateToSearch = MutableLiveData<NavigateToSearch>()
    val navigateToSearch: LiveData<NavigateToSearch>
        get() = _navigateToSearch

    fun onFabClicked() {
        _navigateToSearch.value = NavigateToSearch
    }

    fun onNavigatedToSearch() {
        _navigateToSearch.value = null
    }
}

object NavigateToSearch