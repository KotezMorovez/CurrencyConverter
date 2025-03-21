package com.morovez.currencyconverter.ui.model

data class EventInfo(
    val isEnoughFunds: Boolean,
    val addValue: String,
    val walletCurrency: String,
    val walletBalance: String,
    val allWalletsInfo: String
)