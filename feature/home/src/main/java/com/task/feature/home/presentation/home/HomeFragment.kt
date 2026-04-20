package com.task.feature.home.presentation.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.task.core.presentation.base.MviFragment
import com.task.feature.home.R
import com.task.feature.home.databinding.FragmentHomeBinding
import com.task.feature.home.presentation.home.adapter.CategoriesPagerAdapter
import com.task.feature.home.presentation.home.adapter.ProductsAdapter
import com.task.feature.home.presentation.home.mvi.HomeAction
import com.task.feature.home.presentation.home.mvi.HomeEffect
import com.task.feature.home.presentation.home.mvi.HomeIntent
import com.task.feature.home.presentation.home.mvi.HomeState
import com.task.feature.home.presentation.models.CategoryUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : MviFragment<HomeState, HomeAction, HomeIntent, HomeEffect, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by viewModel(ownerProducer = { requireActivity() })

    private var _binding: FragmentHomeBinding? = null
    override val binding get() = _binding!!

    private val categoriesAdapter = CategoriesPagerAdapter()
    private val productsAdapter = ProductsAdapter()

    private var lastCategories: List<CategoryUI> = emptyList()
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateDots(position)
            val categoryId = categoriesAdapter.getItemAt(position)?.id ?: return
            intent(HomeIntent.OnCategoryChange(categoryId))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupCarousel()
        setupProductList()
        setupSearch()
        setupFab()
    }

    override fun renderState(state: HomeState) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        if (state.categories != lastCategories) {
            lastCategories = state.categories
            categoriesAdapter.submitList(state.categories)
            buildDots(state.categories.size)
            if (state.categories.isNotEmpty()) updateDots(0)
        }

        productsAdapter.submitList(state.products)
    }

    override fun handleEffect(effect: HomeEffect) {
        when (effect) {
            is HomeEffect.ShowError ->
                Snackbar.make(binding.root, effect.message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupCarousel() {
        val horizontalPaddingPx = resources.getDimensionPixelSize(R.dimen.carousel_padding_horizontal)
        val verticalPaddingPx = resources.getDimensionPixelSize(R.dimen.carousel_padding_vertical)
        val pageMarginPx = resources.getDimensionPixelSize(R.dimen.carousel_page_margin)

        binding.viewPager.apply {
            adapter = categoriesAdapter
            offscreenPageLimit = 1
            clipToPadding = false
            clipChildren = false
            setPadding(horizontalPaddingPx, verticalPaddingPx, horizontalPaddingPx, verticalPaddingPx)
            setPageTransformer(MarginPageTransformer(pageMarginPx))
            registerOnPageChangeCallback(pageChangeCallback)
        }
    }

    private fun setupProductList() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productsAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                intent(HomeIntent.Search(s?.toString().orEmpty()))
            }
        })
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            intent(HomeIntent.LoadStatistics)
            StatsBottomSheetFragment().show(childFragmentManager, StatsBottomSheetFragment.TAG)
        }
    }

    private fun buildDots(count: Int) {
        binding.dotsContainer.removeAllViews()
        val size = resources.getDimensionPixelSize(R.dimen.dot_size)
        val margin = resources.getDimensionPixelSize(R.dimen.dot_margin)
        repeat(count) {
            val dot = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(margin, 0, margin, 0)
                }
                setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dot_inactive))
            }
            binding.dotsContainer.addView(dot)
        }
    }

    private fun updateDots(activeIndex: Int) {
        val container = binding.dotsContainer
        for (i in 0 until container.childCount) {
            val dot = container.getChildAt(i) as? ImageView ?: continue
            val drawableRes = if (i == activeIndex) R.drawable.dot_active else R.drawable.dot_inactive
            dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), drawableRes))
        }
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroyView()
        _binding = null
    }
}
