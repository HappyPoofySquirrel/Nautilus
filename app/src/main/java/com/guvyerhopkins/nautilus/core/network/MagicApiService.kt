package com.guvyerhopkins.nautilus.core.network

import com.guvyerhopkins.nautilus.BuildConfig.MAGIC_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }.build())
    .baseUrl(MAGIC_API_BASE_URL)
    .build()

interface MagicApiService {
    @GET("cards")
    suspend fun getCards(
        @Query("name") query: String,
        @Query("page") page: Int,
        @Query("pageSize") perPage: Int
    ): MagicCardsResponse
}

object MagicApi {
    val retrofitSeservice: MagicApiService by lazy {
        retrofit.create(MagicApiService::class.java)
    }
}