package com.morovez.currencyconverter.data.account

import com.morovez.currencyconverter.data.account.dto.AccountInfoEntity
import com.morovez.currencyconverter.data.account.dto.ExchangeInfoEntity
import com.morovez.currencyconverter.domain.account.model.AccountInfo
import com.morovez.currencyconverter.domain.account.model.AccountInfoCurrency
import com.morovez.currencyconverter.domain.account.model.ExchangeInfo

fun AccountInfoEntity.toDomain(): AccountInfo? {
    val currency = when(this.currency) {
        "eur" -> AccountInfoCurrency.EUR
        "usd" -> AccountInfoCurrency.USD
        "gbp" -> AccountInfoCurrency.GBP
        else -> null
    } ?: return null

    return AccountInfo(
        name = this.name,
        value = this.value,
        currency = currency
    )
}

fun AccountInfo.toData() = AccountInfoEntity(
    name = this.name,
    value = this.value,
    currency = when (this.currency) {
        AccountInfoCurrency.EUR -> "eur"
        AccountInfoCurrency.USD -> "usd"
        AccountInfoCurrency.GBP -> "gbp"
    }
)

fun ExchangeInfo.toData() = ExchangeInfoEntity(
    newAccountFromInfo = this.newAccountFromInfo.toData(),
    newAccountToInfo = this.newAccountToInfo.toData()
)