package com.morovez.currencyconverter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morovez.currencyconverter.ui.adapters.CardItemUI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var cardFromListUI: List<CardItemUI> = listOf(
        CardItemUI(
            currencyName = "TEST1",
            walletTotal = "100.00",
            symbol = "-",
            relevantExchangeRate = "1test = 10test",
            currentValue = ""
        ),
        CardItemUI(
            currencyName = "TEST2",
            walletTotal = "100.00",
            symbol = "-",
            relevantExchangeRate = "11test = 10test",
            currentValue = ""
        ),
    )
    private var cardToListUI: List<CardItemUI> = listOf(
        CardItemUI(
            currencyName = "TEST3",
            walletTotal = "100.00",
            symbol = "+",
            relevantExchangeRate = "1test = 0.1test",
            currentValue = ""
        ),
        CardItemUI(
            currencyName = "TEST4",
            walletTotal = "100.00",
            symbol = "+",
            relevantExchangeRate = "1test = 0.1test",
            currentValue = ""
        )
    )
    private var positionFrom = 0
    private var positionTo = 0
    private var valueFrom: Float = 0F
    private var valueTo: Float = 0F

    private val _toolbarState: MutableStateFlow<String> = MutableStateFlow("")
    val toolbarState = _toolbarState.asStateFlow()

    private val _stateFlow: MutableStateFlow<State> = MutableStateFlow(
        State(
            cardFromList = cardFromListUI,
            cardToList = cardToListUI
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    private val _exchangeEvent = MutableSharedFlow<EventInfo>(0)
    val exchangeEvent = _exchangeEvent.asSharedFlow()

    fun evaluate(value: Float, isCardFrom: Boolean) {
        val newFromList = mutableListOf<CardItemUI>()
        val newToList = mutableListOf<CardItemUI>()

        if (isCardFrom) {
            valueFrom = value
            valueTo = value / 10 // TODO

            newFromList.addAll(cardFromListUI)
            newFromList[positionFrom] = newFromList[positionFrom].copy(
                currentValue = value.toString()
            )

            for (card in cardToListUI) {
                newToList.add(card.copy(currentValue = valueTo.toString()))
            }

        } else {
            valueTo = value
            valueFrom = value * 10 // TODO
            newToList.addAll(cardToListUI)

            for (card in cardFromListUI) {
                newFromList.add(card.copy(currentValue = valueFrom.toString()))
            }
        }

        _stateFlow.value = State(newFromList, newToList)
        cardFromListUI = newFromList
        cardToListUI = newToList
    }

    fun scrollEvent(isFrom: Boolean, position: Int) {
        if (isFrom) {
            valueFrom = 0F
            valueTo = 0F

            val newListFrom = mutableListOf<CardItemUI>()
            val newListTo = mutableListOf<CardItemUI>()

            for (card in cardFromListUI) {
                newListFrom.add(card.copy(currentValue = ""))
            }
            for (card in cardToListUI) {
                newListTo.add(card.copy(currentValue = ""))
            }

            _toolbarState.value = cardFromListUI[position].relevantExchangeRate
            _stateFlow.value = State(newListFrom, newListTo)
        } else {
            // evaluate relevant exchange rate
        }
    }

    fun exchange() {
        if (valueFrom == 0f) return

        val isEnoughFunds = valueFrom <= 100.0F // TODO
        val walletValue = valueTo
        val walletCurrency = cardToListUI[positionTo].currencyName
        val walletBalance = (100 + valueTo).toString()

        viewModelScope.launch {
            _exchangeEvent.emit(
                EventInfo(
                    isEnoughFunds = isEnoughFunds,
                    addValue = walletValue.toString(),
                    walletCurrency = walletCurrency,
                    walletBalance = walletBalance,
                    allWalletsInfo = "EUR: 100\nUSD: 100\nRUB: 100" // TODO
                )
            )
        }
    }

    data class State(
        val cardFromList: List<CardItemUI>,
        val cardToList: List<CardItemUI>
    )
}

data class EventInfo(
    val isEnoughFunds: Boolean,
    val addValue: String,
    val walletCurrency: String,
    val walletBalance: String,
    val allWalletsInfo: String
)