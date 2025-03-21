package com.morovez.currencyconverter.domain.currency_rate.repository_api

import com.morovez.currencyconverter.domain.currency_rate.model.CurrencyRate
import io.reactivex.Observable

interface CurrencyRepository {
    fun getData(): Observable<CurrencyRate>
}