package com.morovez.currencyconverter.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.morovez.currencyconverter.R
import com.morovez.currencyconverter.databinding.ActivityMainBinding
import com.morovez.currencyconverter.di.AppComponentHolder
import com.morovez.currencyconverter.di.ViewModelFactory
import com.morovez.currencyconverter.ui.adapter.CardAdapter
import com.morovez.currencyconverter.ui.adapter.ItemDecoration
import com.morovez.currencyconverter.ui.common.collectWithLifecycle
import com.morovez.currencyconverter.ui.model.EventInfo
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var factory: ViewModelFactory<MainViewModel>
    private val viewModel by lazy {
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private val cardFromAdapter: CardAdapter by lazy {
        CardAdapter { value: String ->
            viewModel.onTextInputChanged(value, true)
        }
    }
    private val cardToAdapter: CardAdapter by lazy {
        CardAdapter { value: String ->
            viewModel.onTextInputChanged(value, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        AppComponentHolder.get().inject(this)

        initUI()
        observeData()

        viewModel.initDataTimer()
    }

    override fun onDestroy() {
        viewModel.stopDataTimer()
        super.onDestroy()
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
                            viewModel.onScrolledToPosition(position, true)
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
                        if (position in 0..<cardToAdapter.itemCount) {
                            viewModel.onScrolledToPosition(position, false)
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
                viewModel.onExchangeButtonTap()
            }

            else -> Unit
        }
    }

    private fun observeData() {
        viewModel.state.collectWithLifecycle(this) { state: MainViewModel.State ->
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