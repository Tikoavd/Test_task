package com.task.core.domain.dispatchers

import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single

@Single
internal class CoroutineDispatchersProvider : DispatchersProvider {
    override val main by lazy { Dispatchers.Main }
    override val io by lazy { Dispatchers.IO }
    override val default by lazy { Dispatchers.Default }
}
