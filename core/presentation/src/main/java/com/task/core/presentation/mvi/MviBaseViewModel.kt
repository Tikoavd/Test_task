package com.task.core.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

abstract class MviBaseViewModel<S : MviState, A : MviAction, I : MviIntent, E : MviEffect>(
    initialState: S,
    reducer: Reducer<A, S>
) : ViewModel(), KoinComponent {

    private val _viewState = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    private val actions = MutableSharedFlow<A>()

    private val _effects = Channel<E>(capacity = Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        actions.onEach { action ->
            _viewState.update { reducer.reduce(action, it) }
        }.launchIn(viewModelScope)
    }

    protected abstract fun handleIntent(intent: I)

    fun onIntent(intent: I) {
        handleIntent(intent)
    }

    protected fun onAction(action: A) {
        viewModelScope.launch {
            actions.emit(action)
        }
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
