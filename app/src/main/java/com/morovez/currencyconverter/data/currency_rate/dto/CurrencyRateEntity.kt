package com.morovez.currencyconverter.data.currency_rate.dto

import com.google.gson.annotations.SerializedName

data class CurrencyRateEntity(
    val rates: Rates
) {
    data class Rates(
        @SerializedName("EUR")
        val eur: Double,
        @SerializedName("USD")
        val usd: Double,
        @SerializedName("GBP")
        val gbp: Double
    )
}
