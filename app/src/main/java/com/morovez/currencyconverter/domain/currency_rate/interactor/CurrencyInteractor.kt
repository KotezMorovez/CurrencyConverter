package com.morovez.currencyconverter.domain.currency_rate.interactor

import com.morovez.currencyconverter.domain.currency_rate.model.CurrencyRate
import com.morovez.currencyconverter.domain.currency_rate.repository_api.CurrencyRepository
import io.reactivex.Observable
import javax.inject.Inject

interface CurrencyInteractor {
    fun getData(): Observable<CurrencyRate>
}

class CurrencyInteractorImpl @Inject constructor(
    private val repository: CurrencyRepository
) : CurrencyInteractor {
    override fun getData(): Observable<CurrencyRate> {
        return repository.getData()
    }
}