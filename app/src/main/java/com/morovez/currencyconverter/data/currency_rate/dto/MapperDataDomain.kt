package com.morovez.currencyconverter.data.currency_rate.dto

import com.morovez.currencyconverter.domain.currency_rate.model.CurrencyRate

fun CurrencyRateEntity.toDomain() = CurrencyRate(
    rateList = listOf(
        Pair("EUR", this.rates.eur),
        Pair("USD", this.rates.usd),
        Pair("GBP", this.rates.gbp)
    )
)