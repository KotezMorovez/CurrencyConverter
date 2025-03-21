package com.morovez.currencyconverter.data.account.repository

import com.morovez.currencyconverter.data.account.service.AccountStorage
import com.morovez.currencyconverter.data.account.toDomain
import com.morovez.currencyconverter.domain.account.model.AccountInfo
import com.morovez.currencyconverter.domain.account.model.AccountInfoCurrency
import com.morovez.currencyconverter.domain.account.repository_api.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val service: AccountStorage
) : AccountRepository {

    override fun updateAccountInfo(currency: AccountInfoCurrency, newValue: Double) {
        service.updateAccountInfo(currency, newValue)
    }

    override fun getAccountsInfo(): List<AccountInfo> {
        return service.getAccountsInfo().mapNotNull { it.toDomain() }
    }
}