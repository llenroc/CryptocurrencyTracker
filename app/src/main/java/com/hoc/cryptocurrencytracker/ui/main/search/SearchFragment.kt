package com.hoc.cryptocurrencytracker.ui.main.search

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.transition.Slide
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.hoc.cryptocurrencytracker.R
import com.hoc.cryptocurrencytracker.data.ModelTicker
import com.hoc.cryptocurrencytracker.ui.detail.DetailActivity
import com.hoc.cryptocurrencytracker.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject
import android.support.v4.util.Pair as AndroidPair

class SearchFragment : Fragment(), SearchContract.View {
    private val searchAdapter: SearchAdapter = SearchAdapter()
    @Inject
    lateinit var presenter: SearchContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).mainActivityComponent.inject(this)

        search_recycler.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
            adapter = searchAdapter
            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
                    .apply {
                        setDrawable(context.getDrawable(R.drawable.gray_divider))
                    })
        }

    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
        presenter.subscribe()
    }


    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
        presenter.detachView()
    }

    override fun getClickItemFlowable() = searchAdapter.clickFlowable

    override fun getSearchFlowable() = (activity as MainActivity).getSearchFlowable()

    override fun showMessage(charSequence: CharSequence) {
        view?.let { Snackbar.make(it, charSequence, Snackbar.LENGTH_SHORT).show() }
    }

    override fun showNoResultsFound() {
        TransitionManager.beginDelayedTransition(
                root_search_frag,
                Slide(Gravity.END)
                        .setDuration(200)
                        .setInterpolator(FastOutSlowInInterpolator()))

        text_no_result_found.visibility = View.VISIBLE
        search_recycler.visibility = View.INVISIBLE
    }

    override fun showSearchList(results: List<ModelTicker>) {
        TransitionManager.beginDelayedTransition(
                root_search_frag,
                Slide(Gravity.END)
                        .setDuration(200)
                        .setInterpolator(FastOutSlowInInterpolator()))

        text_no_result_found.visibility = View.INVISIBLE
        search_recycler.visibility = View.VISIBLE

        searchAdapter.submitList(results)
        search_recycler.scheduleLayoutAnimation()
    }

    override fun navigateToDetailActivity(ticker: ModelTicker.Ticker?) {
        val intent = Intent(context, DetailActivity::class.java).apply {
            putExtra(DetailActivity.ITEM, ticker)
        }
        startActivity(intent)
        activity?.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

}
