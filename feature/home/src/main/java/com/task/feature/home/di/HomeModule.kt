package com.task.feature.home.di

import com.task.feature.home.data.api.ProductsApi
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit

@Module
@ComponentScan("com.task.feature.home")
class HomeModule {

    @Single
    fun provideProductsApi(retrofit: Retrofit): ProductsApi =
        retrofit.create(ProductsApi::class.java)
}
