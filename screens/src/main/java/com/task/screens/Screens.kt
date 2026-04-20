package com.task.screens

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
@Stable
sealed class Screens {
    @Serializable
    @Stable
    data object Home : Screens()
}
