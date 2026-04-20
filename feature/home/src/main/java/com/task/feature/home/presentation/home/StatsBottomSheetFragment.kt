package com.task.feature.home.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.task.feature.home.R
import com.task.feature.home.databinding.FragmentStatsBottomSheetBinding
import com.task.feature.home.presentation.models.ProductStatisticsUI
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentStatsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                    binding.progressBar.visibility =
                        if (state.isBottomSheetLoading) View.VISIBLE else View.GONE
                    state.statistics?.let { bindStats(it) }
                }
            }
        }
    }

    private fun bindStats(stats: ProductStatisticsUI) {
        binding.tvItemCount.text = stats.categoryItemCounts.joinToString(separator = "\n") { stat ->
            getString(R.string.item_count_format, stat.categoryName, stat.itemCount)
        }
        binding.tvTopChars.text = stats.topCharacters.joinToString(separator = "\n") { stat ->
            "'${stat.character}' = ${stat.count}"
        }.ifEmpty { getString(R.string.no_data) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "StatsBottomSheetFragment"
    }
}
