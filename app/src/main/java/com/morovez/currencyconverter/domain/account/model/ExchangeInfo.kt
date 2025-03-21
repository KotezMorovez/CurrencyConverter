package com.morovez.currencyconverter.domain.account.model


data class ExchangeInfo(
    val newAccountFromInfo: AccountInfo,
    val newAccountToInfo: AccountInfo,
)
