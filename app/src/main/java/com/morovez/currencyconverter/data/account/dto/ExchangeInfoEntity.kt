package com.morovez.currencyconverter.data.account.dto

data class ExchangeInfoEntity(
    val newAccountFromInfo: AccountInfoEntity,
    val newAccountToInfo: AccountInfoEntity,
)
