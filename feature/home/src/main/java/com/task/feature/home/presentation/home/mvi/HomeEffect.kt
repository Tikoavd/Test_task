package com.task.feature.home.presentation.home.mvi

import com.task.core.presentation.mvi.MviEffect

sealed interface HomeEffect : MviEffect {
    data class ShowError(val message: String) : HomeEffect
}
