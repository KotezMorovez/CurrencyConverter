package com.morovez.currencyconverter.domain.currency_rate.model

data class CurrencyRate(
    val rateList: List<Pair<String, Double>>
)
