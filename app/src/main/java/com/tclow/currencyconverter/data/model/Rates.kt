package com.tclow.currencyconverter.data.model

import com.mynameismidori.currencypicker.ExtendedCurrency

class Rates {
    var currencyRate: String? = null

    public fun getCurrencyRate(): String {
        return ExtendedCurrency.getCurrencyByISO(currencyRate).code
    }
}