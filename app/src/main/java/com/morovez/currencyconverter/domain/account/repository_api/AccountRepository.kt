package com.morovez.currencyconverter.domain.account.repository_api

import com.morovez.currencyconverter.domain.account.model.AccountInfo
import com.morovez.currencyconverter.domain.account.model.AccountInfoCurrency

interface AccountRepository {
    fun updateAccountInfo(currency: AccountInfoCurrency, newValue: Double)
    fun getAccountsInfo(): List<AccountInfo>
}