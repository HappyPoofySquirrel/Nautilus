package com.guvyerhopkins.nautilus.network

import com.guvyerhopkins.nautilus.BuildConfig.MAGIC_API_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient.Builder().apply {
        addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("Page-Size", "30")
                builder.header("Count", "10")
                return@Interceptor chain.proceed(builder.build())
            }
        )
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
        @Query("per_page") perPage: Int
    ): MagicCardsResponse
}

object MagicApi {
    val retrofitSeservice: MagicApiService by lazy {
        retrofit.create(MagicApiService::class.java)
    }
}
