package com.example.android.gdgfinder.search

import android.location.Location
import android.util.LruCache
import com.example.android.gdgfinder.network.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

class GdgChapterRepository(gdgApiService: GdgApiService) {

    private val request = gdgApiService.getChapters()
    /**
     * Stores the network results parsed in a way that's useful for our queries.
     */
    private lateinit var data: RepositoryData

    /**
     * Get both the chapter list and filters for the current filter and location.
     *
     * Chapters will be sorted by distance from the passed currentLocation. Filters will be (roughly) sorted based on
     * the distance from currentLocation. Filter sorting will use sampling and may miss-sort adjacent regions.
     *
     * @param filter only chapters in the specified region will be returned, or all if null
     * @param currentLocation when not null, chapters and filters will be sorted by the distance to this location
     */
    suspend fun getGdgInformationByLocation(
        filter: String?,
        currentLocation: Location?
    ): GdgChapterInformation {
        // wait for network result before processing (this may get cancelled if another query starts)
        if (!::data.isInitialized) {
            data = RepositoryData(request.await())
        }
        // apply filters and sorting
        return GdgChapterInformation(
            getChaptersSortedByLocation(filter, currentLocation),
            getFiltersSortedByLocation(currentLocation))
    }


    /**
     * Get a list of chapters with the appropriate filter applied.
     *
     * Chapters will be sorted by distance from the passed currentLocation.
     *
     * @param filter only chapters in the specified region will be returned, or all if null
     * @param currentLocation optional location to sort the chapters by
     */
    private suspend fun getChaptersSortedByLocation(filter: String?, currentLocation: Location?): List<GdgChapter> {
        return withContext(Dispatchers.Default) {
            // sorting is too expensive to perform on the Main thread
            chaptersFor(filter).sortByDistanceFrom(currentLocation)
        }
    }

    /**
     * Get a list of filters.
     *
     * Filters will be (roughly) sorted based on the distance from currentLocation. Filter sorting will use
     * sampling and may miss-sort adjacent regions.
     *
     * @param currentLocation optional location to sort the regions by
     */
    private suspend fun getFiltersSortedByLocation(currentLocation: Location?): List<String> {
        if (currentLocation != null) {
            return withContext(Dispatchers.Default) {
                // this logic is too expensive to run on the Main thread, so perform thread confinement

                // first select the first chapter from each region (to make sorting cheaper)
                val sampledChapters = data.chapters.distinctBy { it.region }
                val sorted = sampledChapters
                    // then sort the chapters by distance from the current location
                    .sortByDistanceFrom(currentLocation)
                    // pull the region out of the chapter
                    .map { it.region }
                    // and make it a mutable list so we can insert any regions that have no chapters
                    .toMutableList()

                // if there are any regions in filters that have no chapters, add them to the end of the list
                for (region in data.filters) {
                    if (region !in sorted) {
                        sorted.add(region)
                    }
                }
                // and return the sorted list
                sorted
            }
        } else {
            return data.filters
        }
    }

    /**
     * Sort a list of GdgChapter by their distance from the specified location.
     *
     * @param currentLocation returned list will be sorted by the distance, or unsorted if null
     */
    private fun List<GdgChapter>.sortByDistanceFrom(currentLocation: Location?): List<GdgChapter> {
        currentLocation ?: return this

        // sorting is too expensive to perform on the Main thread, but since this is a private function we can ensure
        // that it's always wrapped by a caller
        return sortedBy { distanceBetween(it.geo, currentLocation)}
    }

    /**
     * Lookup the (unsorted) chapters list for a filter
     */
    private fun chaptersFor(filter: String?): List<GdgChapter> {
        return when(filter) {
            null -> data.chapters
            else -> data.chaptersByRegion.getOrElse(filter) { emptyList() }
        }
    }

    /**
     * Calculate the distance (in meters) between a LatLong and a Location.
     */
    private fun distanceBetween(start: LatLong, currentLocation: Location): Float {
        val results = FloatArray(3)
        Location.distanceBetween(start.lat, start.long, currentLocation.latitude, currentLocation.longitude, results)
        return results[0]
    }

    /**
     * Result object for [GdgChapterRepository] to allow callers to fetch both from the same list of filters.
     */
    data class GdgChapterInformation(val chapters: List<GdgChapter>, val filters: List<String>)

    /**
     * Represents the parsed network result for our Repository
     */
    private class RepositoryData(response: GdgResponse) {
        val chapters = response.chapters
        val filters = response.filters.regions

        // profiling shows that groupBy is not too expensive to run on the Main thread for our data set, so no need
        // for thread confinement here (always measure when not sure!)
        val chaptersByRegion = chapters.groupBy { it.region }
    }
}