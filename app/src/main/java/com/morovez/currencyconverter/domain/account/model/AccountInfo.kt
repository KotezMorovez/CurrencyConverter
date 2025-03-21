package com.morovez.currencyconverter.domain.account.model

data class AccountInfo(
    val name: String,
    var value: Double,
    val currency: AccountInfoCurrency
)