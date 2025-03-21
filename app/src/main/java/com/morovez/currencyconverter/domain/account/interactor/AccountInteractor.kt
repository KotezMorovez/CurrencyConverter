package com.morovez.currencyconverter.domain.account.interactor

import com.morovez.currencyconverter.domain.account.model.AccountInfo
import com.morovez.currencyconverter.domain.account.model.AccountInfoCurrency
import com.morovez.currencyconverter.domain.account.repository_api.AccountRepository
import javax.inject.Inject

interface AccountInteractor {
    fun exchange(
        valueFrom: Double,
        valueTo: Double,
        currencyFrom: AccountInfoCurrency,
        currencyTo: AccountInfoCurrency
    )

    fun getAccountsInfo(): List<AccountInfo>
}

class AccountInteractorImpl @Inject constructor(
    private val repository: AccountRepository
) : AccountInteractor {
    override fun exchange(
        valueFrom: Double,
        valueTo: Double,
        currencyFrom: AccountInfoCurrency,
        currencyTo: AccountInfoCurrency
    ) {
        var accountsList = getAccountsInfo()
        var newValueFrom = 0.0
        var newValueTo = 0.0

        for (account in accountsList) {
            if (account.currency == currencyFrom) {
                newValueFrom = account.value - valueFrom
            }
        }

        repository.updateAccountInfo(currencyFrom, newValueFrom)
        accountsList = getAccountsInfo()

        for (account in accountsList) {
            if (account.currency == currencyTo) {
                newValueTo = account.value + valueTo
            }
        }

        repository.updateAccountInfo(currencyTo, newValueTo)
    }


    override fun getAccountsInfo(): List<AccountInfo> {
        return repository.getAccountsInfo()
    }
}