package com.morovez.currencyconverter.ui.adapters

data class CardItemUI (
    val currencyName: String,
    val walletTotal: String,
    val symbol: String,
    val relevantExchangeRate: String,
    val currentValue: String
)