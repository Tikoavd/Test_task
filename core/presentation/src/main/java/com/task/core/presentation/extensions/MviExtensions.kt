package com.task.core.presentation.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.task.core.presentation.mvi.MviAction
import com.task.core.presentation.mvi.MviBaseViewModel
import com.task.core.presentation.mvi.MviEffect
import com.task.core.presentation.mvi.MviIntent
import com.task.core.presentation.mvi.MviState
import kotlinx.coroutines.CoroutineScope

@SuppressLint("ComposableNaming")
@Composable
fun <S : MviState, A : MviAction, I : MviIntent, E : MviEffect>
        MviBaseViewModel<S, A, I, E>.collectEffects(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    sideEffect: (suspend CoroutineScope.(effect: E) -> Unit)
) {
    val effectsFlow = effects
    val lifecycleOwner = LocalLifecycleOwner.current

    val callback by rememberUpdatedState(newValue = sideEffect)

    LaunchedEffect(effectsFlow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            effectsFlow.collect { callback(it) }
        }
    }
}