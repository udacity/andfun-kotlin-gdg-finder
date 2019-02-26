package com.example.android.gdgfinder.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://developers.google.com/community/gdg/directory/"
interface GdgApiService {
    @GET("directory.json")
    fun getChapters():
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
            Deferred<GdgResponse>
}
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

object GdgApi {
    val retrofitService : GdgApiService by lazy { retrofit.create(GdgApiService::class.java) }
}
