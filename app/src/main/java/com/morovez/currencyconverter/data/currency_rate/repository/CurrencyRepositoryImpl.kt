package com.morovez.currencyconverter.data.currency_rate.repository

import com.morovez.currencyconverter.data.currency_rate.dto.toDomain
import com.morovez.currencyconverter.data.currency_rate.service.NetworkService
import com.morovez.currencyconverter.domain.currency_rate.model.CurrencyRate
import com.morovez.currencyconverter.domain.currency_rate.repository_api.CurrencyRepository
import io.reactivex.Observable
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val service: NetworkService
) : CurrencyRepository {
    override fun getData(): Observable<CurrencyRate> {
        return service.getCurrencyRate().map { it.toDomain() }
    }
}