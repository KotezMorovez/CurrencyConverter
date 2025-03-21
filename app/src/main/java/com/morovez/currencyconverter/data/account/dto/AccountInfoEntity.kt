package com.morovez.currencyconverter.data.account.dto

data class AccountInfoEntity(
    val name: String,
    val currency: String,
    var value: Double = 100.0,
)
