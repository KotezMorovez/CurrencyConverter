package com.morovez.currencyconverter.ui.model

data class CardItemUI (
    val currencyName: String,
    val walletTotal: String,
    val symbol: String,
    val relevantExchangeRate: String,
    val currentValue: String
)