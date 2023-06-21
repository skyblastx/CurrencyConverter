package com.tclow.currencyconverter.main

import com.tclow.currencyconverter.data.model.CurrencyResponse
import com.tclow.currencyconverter.database.dao.CurrencyRate
import com.tclow.currencyconverter.util.Resource

interface BaseRepository {

    suspend fun getRates(): Resource<CurrencyResponse>
    suspend fun getRates(base: String): Resource<CurrencyResponse>
    suspend fun getHistoricalRates(date: String): Resource<CurrencyResponse>
}