package com.task.core.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.task.core.data.BuildConfig
import com.task.core.data.CONTENT_TYPE
import com.task.core.data.provider.UrlProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit

@Module
@ComponentScan("com.task.core.data")
class CoreDataModule {
    @Single
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Single
    fun providesOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient = builder.build()

    @Single
    fun providesOkHttpClientBuilder(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient.Builder = OkHttpClient
        .Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    httpLoggingInterceptor
                )
            }
        }

    @Single
    fun provideRetrofit(json: Json, okHttpClient: OkHttpClient, urlProvider: UrlProvider): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlProvider.baseUrl)
            .addConverterFactory(json.asConverterFactory(CONTENT_TYPE.toMediaType()))
            .client(okHttpClient)
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Single
    fun provideJson() = Json {
        isLenient = true
        ignoreUnknownKeys = true
        allowSpecialFloatingPointValues = true
        useArrayPolymorphism = false
        prettyPrint = true
        coerceInputValues = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        explicitNulls = true
    }
}
