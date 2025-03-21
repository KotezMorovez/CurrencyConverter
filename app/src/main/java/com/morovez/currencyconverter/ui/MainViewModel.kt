package com.morovez.currencyconverter.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morovez.currencyconverter.R
import com.morovez.currencyconverter.domain.account.interactor.AccountInteractor
import com.morovez.currencyconverter.domain.account.model.AccountInfo
import com.morovez.currencyconverter.domain.currency_rate.interactor.CurrencyInteractor
import com.morovez.currencyconverter.domain.currency_rate.model.CurrencyRate
import com.morovez.currencyconverter.ui.common.ResourceProvider
import com.morovez.currencyconverter.ui.model.CardItemUI
import com.morovez.currencyconverter.ui.model.EventInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val currencyInteractor: CurrencyInteractor,
    private val accountInteractor: AccountInteractor
) : ViewModel() {
    private var accountsDomainList: List<AccountInfo> = listOf()
    private var currentCurrencyRate: CurrencyRate? = null

    private var accountFromListUI: List<CardItemUI> = listOf()
    private var accountToListUI: List<CardItemUI> = listOf()

    private var positionFrom = DEFAULT_POSITION
    private var positionTo = DEFAULT_POSITION

    private var valueFrom: Double = DEFAULT_VALUE
    private var valueTo: Double = DEFAULT_VALUE

    private var timerDisposable: Disposable? = null

    private val _toolbarState: MutableStateFlow<String> = MutableStateFlow("")
    val toolbarState = _toolbarState.asStateFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        State(
            cardFromList = accountFromListUI,
            cardToList = accountToListUI
        )
    )
    val state = _state.asStateFlow()

    private val _exchangeEvent = MutableSharedFlow<EventInfo>(0)
    val exchangeEvent = _exchangeEvent.asSharedFlow()

    fun initDataTimer() {
        timerDisposable?.dispose()
        timerDisposable = Observable
            .interval(TIMER_INTERVAL, TimeUnit.SECONDS)
            .filter { it % 30 == 0L }
            .flatMap {
                currencyInteractor.getData()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { currencyRate: CurrencyRate ->
                    currentCurrencyRate = currencyRate
                    accountsDomainList = accountInteractor.getAccountsInfo()
                    updateCardsInfo()
                },
                { error: Throwable ->
                    Log.e("ERROR", "$error")
                }
            )
    }

    fun stopDataTimer() {
        timerDisposable?.dispose()
    }

    fun onTextInputChanged(value: String, isCardFrom: Boolean) {
        var newFromList = mutableListOf<CardItemUI>()
        var newToList = mutableListOf<CardItemUI>()

        val rates = getValueAndResultRates(isCardFrom.not())
        val doubleValue = value.toDouble()

        if (isCardFrom) {
            valueFrom = doubleValue
            valueTo = calculateExchangeValue(doubleValue, rates.first, rates.second)

            newFromList = accountFromListUI.toMutableList()
            newFromList[positionFrom] = newFromList[positionFrom].copy(
                currentValue = if (value == DEFAULT_VALUE.toString()){
                    EMPTY_STRING
                } else {
                    value
                }
            )

            for (card in accountToListUI) {
                newToList.add(card.copy(currentValue = getFormattedValue(valueTo)))
            }

        } else {
            valueTo = doubleValue
            valueFrom = calculateExchangeValue(doubleValue, rates.first, rates.second)
            newToList = accountToListUI.toMutableList()

            for (card in accountFromListUI) {
                newFromList.add(card.copy(currentValue = getFormattedValue(valueFrom)))
            }
        }

        _state.value = State(newFromList, newToList)
        accountFromListUI = newFromList
        accountToListUI = newToList
    }

    fun onScrolledToPosition(position: Int, isCardFrom: Boolean) {
        val newListFrom: MutableList<CardItemUI>
        val newListTo: MutableList<CardItemUI>

        if (isCardFrom) {
            valueFrom = DEFAULT_VALUE
            valueTo = DEFAULT_VALUE
            positionFrom = position

            newListFrom = accountFromListUI.map { it.copy(currentValue = EMPTY_STRING) }.toMutableList()
            newListTo = accountToListUI.map { it.copy(currentValue = EMPTY_STRING) }.toMutableList()

        } else {
            positionTo = position

            newListFrom = accountFromListUI.toMutableList()
            newListTo = accountToListUI.toMutableList()
        }

        val rates = getValueAndResultRates(isCardFrom)

        val newFromExchangeValue = calculateExchangeValue(
            DEFAULT_EXCHANGE_RATE,
            rates.first,
            rates.second
        )
        val newToExchangeValue = calculateExchangeValue(
            DEFAULT_EXCHANGE_RATE,
            rates.second,
            rates.first
        )

        newListFrom[positionFrom] = newListFrom[positionFrom].copy(
            relevantExchangeRate = getFormattedExchangeRateString(
                accountsDomainList[positionFrom].currency.symbol,
                accountsDomainList[positionTo].currency.symbol,
                newFromExchangeValue
            )
        )

        newListTo[positionTo] = newListTo[positionTo].copy(
            relevantExchangeRate = getFormattedExchangeRateString(
                accountsDomainList[positionTo].currency.symbol,
                accountsDomainList[positionFrom].currency.symbol,
                newToExchangeValue
            )
        )

        _toolbarState.value = newListFrom[positionFrom].relevantExchangeRate
        _state.value = State(newListFrom, newListTo)

        accountFromListUI = newListFrom
        accountToListUI = newListTo

        onTextInputChanged(valueFrom.toString(), true)
    }

    fun onExchangeButtonTap() {
        if (valueFrom == DEFAULT_VALUE) return

        val isEnoughFunds = valueFrom <= accountsDomainList[positionFrom].value

        if (isEnoughFunds) {
            accountInteractor.exchange(
                valueFrom,
                valueTo,
                accountsDomainList[positionFrom].currency,
                accountsDomainList[positionTo].currency
            )
            accountsDomainList = accountInteractor.getAccountsInfo()
        }

        val walletValue = getFormattedValue(valueTo)
        val walletCurrency = accountToListUI[positionTo].currencyName
        val walletBalance = getFormattedValue(accountsDomainList[positionTo].value)

        viewModelScope.launch {
            _exchangeEvent.emit(
                EventInfo(
                    isEnoughFunds = isEnoughFunds,
                    addValue = walletValue,
                    walletCurrency = walletCurrency,
                    walletBalance = walletBalance,
                    allWalletsInfo = getAllWalletsInfo()
                )
            )
        }

        valueTo = DEFAULT_VALUE
        valueFrom = DEFAULT_VALUE

        updateCardsInfo()
    }

    private fun updateCardsInfo() {
        val fromRates = getValueAndResultRates(true)
        val toRates = getValueAndResultRates(false)
        val symbolFrom = resourceProvider.getString(R.string.symbol_from)
        val symbolTo = resourceProvider.getString(R.string.symbol_to)

        val newAccountsFromListUI = accountsDomainList.map {
            CardItemUI(
                currencyName = it.name,
                walletTotal = getWalletTotal(it.value, it.currency.symbol),
                symbol = symbolFrom,
                relevantExchangeRate = getFormattedExchangeRateString(
                    it.currency.symbol,
                    accountsDomainList[positionTo].currency.symbol,
                    calculateExchangeValue(
                        DEFAULT_EXCHANGE_RATE,
                        fromRates.first,
                        fromRates.second
                    )
                ),
                currentValue = if (valueFrom == DEFAULT_VALUE) {
                    EMPTY_STRING
                } else {
                    getFormattedValue(valueFrom)
                }
            )
        }

        val newAccountsToListUI = accountsDomainList.map {
            CardItemUI(
                currencyName = it.name,
                walletTotal = getWalletTotal(it.value, it.currency.symbol),
                symbol = symbolTo,
                relevantExchangeRate = getFormattedExchangeRateString(
                    it.currency.symbol,
                    accountsDomainList[positionFrom].currency.symbol,
                    calculateExchangeValue(
                        DEFAULT_EXCHANGE_RATE,
                        toRates.first,
                        toRates.second
                    )
                ),
                currentValue = if (valueTo == DEFAULT_VALUE) {
                    EMPTY_STRING
                } else {
                    getFormattedValue(valueTo)
                }
            )
        }

        _state.value = State(newAccountsFromListUI, newAccountsToListUI)

        accountFromListUI = newAccountsFromListUI
        accountToListUI = newAccountsToListUI
    }

    private fun calculateExchangeValue(
        value: Double,
        valueRate: Double,
        resultRate: Double
    ) = (valueRate * value) / resultRate

    private fun getValueAndResultRates(isFrom: Boolean): Pair<Double, Double> {
        currentCurrencyRate?.let {
            val valueRate = if (isFrom) {
                it.rateList[positionFrom].second
            } else {
                it.rateList[positionTo].second
            }
            val resultRate = if (isFrom) {
                it.rateList[positionTo].second
            } else {
                it.rateList[positionFrom].second
            }
            return Pair(valueRate, resultRate)
        }
        return Pair(DEFAULT_VALUE, DEFAULT_VALUE)
    }

    private fun getFormattedExchangeRateString(
        fromSymbol: String,
        toSymbol: String,
        exchangeValue: Double
    ) = "${fromSymbol}1.00=${toSymbol}${getFormattedValue(exchangeValue)}"

    private fun getWalletTotal(value: Double, symbol: String) =
        resourceProvider.getString(R.string.card_wallet) + "${getFormattedValue(value)}$symbol"

    private fun getAllWalletsInfo() =
        accountsDomainList.joinToString(separator = "\n") {
            "${it.name}: ${getFormattedValue(it.value)}"
        }

    private fun getFormattedValue(value: Double) =
        if (value == DEFAULT_VALUE) {
            EMPTY_STRING
        } else {
            String.format("%.2f", value)
        }

    companion object {
        private const val DEFAULT_POSITION = 0
        private const val DEFAULT_EXCHANGE_RATE = 1.0
        private const val DEFAULT_VALUE = 0.0
        private const val TIMER_INTERVAL = 1L
        private const val EMPTY_STRING = ""
    }

    data class State(
        val cardFromList: List<CardItemUI>,
        val cardToList: List<CardItemUI>
    )
}