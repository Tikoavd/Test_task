package com.task.core.presentation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.task.core.presentation.mvi.MviAction
import com.task.core.presentation.mvi.MviBaseViewModel
import com.task.core.presentation.mvi.MviEffect
import com.task.core.presentation.mvi.MviIntent
import com.task.core.presentation.mvi.MviState
import kotlinx.coroutines.launch

abstract class MviFragment<
        S : MviState,
        A : MviAction,
        I : MviIntent,
        E : MviEffect,
        VB : ViewBinding
        > : Fragment() {

    protected abstract val viewModel: MviBaseViewModel<S, A, I, E>
    protected abstract val binding: VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.viewState.collect { renderState(it) } }
                launch { viewModel.effects.collect { handleEffect(it) } }
            }
        }
    }

    protected abstract fun setupViews()
    protected abstract fun renderState(state: S)
    protected open fun handleEffect(effect: E) {}

    protected fun intent(intent: I) = viewModel.onIntent(intent)
}
