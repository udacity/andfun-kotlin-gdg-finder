package com.example.android.gdgfinder.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize



@Parcelize
data class GdgChapter(
    @Json(name = "chapter_name") val name: String,
    @Json(name = "cityarea") val city: String,
    val country: String,
    val region: String,
    val website: String,
    val geo: LatLong
 ): Parcelable

@Parcelize
data class LatLong(
    val lat: Double,
    @Json(name = "lng")
    val long: Double
) : Parcelable

@Parcelize
data class GdgResponse(
        @Json(name = "filters_") val filters: Filter,
        @Json(name = "data") val chapters: List<GdgChapter>
): Parcelable

@Parcelize
data class Filter(
        @Json(name = "region") val regions: List<String>
): Parcelable

//"chapter_name": "GDG Bordj Bou-Arréridj",
//"cityarea": "Burj Bu Arririj",
//"country": "Algeria",
//"region": "Africa",
//"website": "https://www.meetup.com/GDG-BBA",
//"geo": {
//    "lat": 36.06000137,
//    "lng": 4.630000114
//}