package com.tclow.currencyconverter.data.model

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: String,
    val success: Boolean,
    val timestamp: Int
)