package com.morovez.currencyconverter.data.currency_rate.service

import com.morovez.currencyconverter.data.currency_rate.dto.CurrencyRateEntity
import io.reactivex.Observable
import retrofit2.http.GET

interface Api {
    @GET("/latest.js")
    fun getCurrencyRate(): Observable<CurrencyRateEntity>
}