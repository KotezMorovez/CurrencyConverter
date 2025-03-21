package com.morovez.currencyconverter.data.currency_rate.service

import com.morovez.currencyconverter.data.currency_rate.dto.CurrencyRateEntity
import io.reactivex.Observable
import javax.inject.Inject

interface NetworkService {
    fun getCurrencyRate(): Observable<CurrencyRateEntity>
}

class NetworkServiceImpl @Inject constructor(
    private val api: Api
) : NetworkService {
    override fun getCurrencyRate(): Observable<CurrencyRateEntity>{
        return api.getCurrencyRate()
    }

}