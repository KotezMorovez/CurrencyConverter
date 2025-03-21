package com.morovez.currencyconverter.data.account.service

import com.morovez.currencyconverter.data.account.dto.AccountInfoEntity
import com.morovez.currencyconverter.domain.account.model.AccountInfoCurrency
import javax.inject.Inject

interface AccountStorage {
    fun updateAccountInfo(currency: AccountInfoCurrency, value: Double)
    fun getAccountsInfo(): List<AccountInfoEntity>
}

class AccountStorageImpl @Inject constructor() : AccountStorage {
    private val accountsList = listOf(
        AccountInfoEntity("EUR", "eur"),
        AccountInfoEntity("USD", "usd"),
        AccountInfoEntity("GBP", "gbp")
    )

    override fun updateAccountInfo(currency: AccountInfoCurrency, value: Double) {
        for (account in accountsList) {
            if (account.currency.equals(currency.name, true)) {
                account.value = value
            }
        }
    }

    override fun getAccountsInfo(): List<AccountInfoEntity> {
        return accountsList
    }
}