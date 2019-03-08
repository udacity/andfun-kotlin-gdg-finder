package com.example.android.gdgfinder.search

import android.location.Location
import com.example.android.gdgfinder.network.*
import kotlinx.coroutines.*

class GdgChapterRepository(gdgApiService: GdgApiService) {

    private val request = gdgApiService.getChapters()
    private var sortedData: SortedData? = null
    private var inProgressSort: Deferred<SortedData>? = null

    var isFullyInitialized = false
        private set


    /**
     * Get the chapters list for a specified filter
     */
    suspend fun getChaptersForFilter(filter: String?): List<GdgChapter> {
        val data = completedData()
        return when(filter) {
            null -> data.chapters
            else -> data.chaptersByRegion.getOrElse(filter) { emptyList() }
        }
    }

    /**
     * Get the cached sorted data, the next computed sorted data, or finally start a new sort.
     */
    private suspend fun GdgChapterRepository.completedData() =
        sortedData ?: inProgressSort?.await() ?: doSortData()

    /**
     * Get the filters sorted by distance from the last location
     */
    suspend fun getFilters(): List<String> = completedData().filters

    /**
     * Call this to force a new sort to start.
     */
    private suspend fun doSortData(location: Location? = null): SortedData {
        val result = coroutineScope {
            // launch a new coroutine so other requests can wait for this sort to complete
            // Also confine sorting to Dispatchres.Default because it's expensive
            val deferred = async(Dispatchers.Default) {
                SortedData(request.await(), location)
            }
            // cache the Deferred so any future requests can wait for this sort
            inProgressSort = deferred
            // and return the result of this sort
            deferred.await()
        }
        // cache the result so we don't have to call this all the time
        sortedData = result
        return result
    }

    /**
     * Call when location changes. This may cancel any previous queries, so it's important to re-request the data
     * after sorting is complete.
     */
    suspend fun onLocationChanged(location: Location) {
        isFullyInitialized = true
        // previous sorts are now invalid, clear out the caches
        sortedData = null

        // cancel any in progress sorts
        inProgressSort?.cancel()

        doSortData(location)
    }

    /**
     * Holds data sorted by the distance from the last location.
     *
     * Note, by convention this class won't be instantiated on the Main thread. This is not a public API and should
     * only be called by [doSortData].
     */
    private class SortedData(response: GdgResponse, location: Location?) {
        val chapters: List<GdgChapter> = response.chapters.sortByDistanceFrom(location)
        val filters: List<String> = chapters.map { it.region } .distinctBy { it }

        var chaptersByRegion: Map<String, List<GdgChapter>> = chapters.groupBy { it.region }

        /**
         * Sort a list of GdgChapter by their distance from the specified location.
         *
         * @param currentLocation returned list will be sorted by the distance, or unsorted if null
         */
        private fun List<GdgChapter>.sortByDistanceFrom(currentLocation: Location?): List<GdgChapter> {
            currentLocation ?: return this

            return sortedBy { distanceBetween(it.geo, currentLocation)}
        }

        /**
         * Calculate the distance (in meters) between a LatLong and a Location.
         */
        private fun distanceBetween(start: LatLong, currentLocation: Location): Float {
            val results = FloatArray(3)
            Location.distanceBetween(start.lat, start.long, currentLocation.latitude, currentLocation.longitude, results)
            return results[0]
        }
    }
}