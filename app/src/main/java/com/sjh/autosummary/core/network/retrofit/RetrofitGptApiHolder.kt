package com.sjh.autosummary.core.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sjh.autosummary.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class RetrofitGptApiHolder(
    json: Json,
) {
    private companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
        const val GPT_API_KEY = BuildConfig.GPT_API_KEY
    }

    val retrofitGptApi: RetrofitGptApi =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient
                    .Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val newRequest = chain
                            .request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", "Bearer $GPT_API_KEY")
                            .build()
                        chain.proceed(newRequest)
                    }
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RetrofitGptApi::class.java)
}
