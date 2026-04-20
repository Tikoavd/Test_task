package com.task.core.domain.dispatchers

import kotlin.coroutines.CoroutineContext

interface DispatchersProvider {
    val main: CoroutineContext
    val io: CoroutineContext

    val default: CoroutineContext
}