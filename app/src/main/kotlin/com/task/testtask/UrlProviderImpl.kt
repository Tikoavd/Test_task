package com.task.testtask

import com.task.core.data.provider.UrlProvider
import org.koin.core.annotation.Single

@Single
class UrlProviderImpl(): UrlProvider {
    override val baseUrl: String = BuildConfig.BASE_URL
}
