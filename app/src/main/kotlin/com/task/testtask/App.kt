package com.task.testtask

import android.app.Application
import com.task.core.data.di.CoreDataModule
import com.task.core.domain.di.CoreDomainModule
import com.task.core.presentation.di.CorePresentationModule
import com.task.feature.home.di.HomeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(modules)
        }
    }

    private val modules = listOf(
        AppModule().module,
        CoreDataModule().module,
        CoreDomainModule().module,
        CorePresentationModule().module,
        HomeModule().module,
    )
}