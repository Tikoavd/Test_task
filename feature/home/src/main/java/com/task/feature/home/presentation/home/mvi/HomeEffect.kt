package com.task.feature.home.presentation.home.mvi

import androidx.compose.runtime.Stable
import com.task.core.presentation.mvi.MviEffect

@Stable
sealed interface HomeEffect : MviEffect {
    data class ShowError(val message: String) : HomeEffect
}