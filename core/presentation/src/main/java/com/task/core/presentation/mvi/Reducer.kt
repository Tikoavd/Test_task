package com.task.core.presentation.mvi

interface Reducer<A : MviAction, S : MviState> {

    fun reduce(action: A, state: S): S
}
