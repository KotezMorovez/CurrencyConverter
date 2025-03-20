package com.morovez.currencyconverter.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.morovez.currencyconverter.R
import com.morovez.currencyconverter.databinding.ActivityMainBinding
import com.morovez.currencyconverter.ui.adapters.CardAdapter
import com.morovez.currencyconverter.ui.adapters.ItemDecoration
import com.morovez.currencyconverter.ui.common.collectWithLifecycle

class MainActivity : AppCompatActivity() {
    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<MainViewModel>()

    private val cardFromAdapter: CardAdapter by lazy {
        CardAdapter { value: Float ->
            viewModel.evaluate(value, true)
        }
    }
    private val cardToAdapter: CardAdapter by lazy {
        CardAdapter { value: Float ->
            viewModel.evaluate(value, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initUI()
        observeData()
    }

    private fun initUI() {
        with(viewBinding) {
            toolbar.setOnMenuItemClickListener {
                handleMenuItemClick(it)
                true
            }

            val linearLayoutManagerFrom = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            val linearLayoutManagerTo = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            initCurrencyRecyclerView(
                currencyFromRecyclerView,
                cardFromAdapter,
                linearLayoutManagerFrom,
                object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val position =
                            linearLayoutManagerFrom.findFirstCompletelyVisibleItemPosition()
                        if (position in 0..<cardFromAdapter.itemCount) {
                            viewModel.scrollEvent(true, position)
                        }
                    }
                }
            )

            initCurrencyRecyclerView(
                currencyToRecyclerView,
                cardToAdapter,
                linearLayoutManagerTo,
                object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val position =
                            linearLayoutManagerTo.findFirstCompletelyVisibleItemPosition()
                        if (position in 0..<cardFromAdapter.itemCount) {
                            viewModel.scrollEvent(false, position)
                        }
                    }
                }
            )
        }
    }

    private fun initCurrencyRecyclerView(
        recyclerView: RecyclerView,
        adapter: CardAdapter,
        layoutManager: LinearLayoutManager,
        scrollListener: OnScrollListener
    ) {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ItemDecoration())
        recyclerView.addOnScrollListener(scrollListener)
    }


    private fun handleMenuItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.exchange -> {
                viewModel.exchange()
            }

            else -> Unit
        }
    }

    private fun observeData() {
        viewModel.stateFlow.collectWithLifecycle(this) { state: MainViewModel.State ->
            cardFromAdapter.submitList(state.cardFromList)
            cardToAdapter.submitList(state.cardToList)
        }

        viewModel.toolbarState.collectWithLifecycle(this) { title: String ->
            viewBinding.toolbar.title = title
        }

        viewModel.exchangeEvent.collectWithLifecycle(this) { eventInfo: EventInfo ->
            val title =
                if (eventInfo.isEnoughFunds) {
                    resources.getString(
                        R.string.receipt_message,
                        eventInfo.addValue,
                        eventInfo.walletCurrency,
                        eventInfo.walletBalance
                    )

                } else {
                    resources.getText(R.string.replenishment_error)
                }

            val message =
                if (eventInfo.isEnoughFunds) resources.getString(
                    R.string.receipt_wallet_info,
                    eventInfo.allWalletsInfo
                )
                else ""


            MaterialAlertDialogBuilder(this, R.style.CustomAlert)
                .setTitle(title)
                .setBackground(
                    ResourcesCompat.getDrawable(
                        this.resources,
                        R.drawable.bg_dialog,
                        null
                    )
                )
                .setMessage(message)
                .setPositiveButton(resources.getText(R.string.receipt_positive_button)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }
}