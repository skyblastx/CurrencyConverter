package com.tclow.currencyconverter.main

import com.tclow.currencyconverter.data.model.CurrencyResponse
import com.tclow.currencyconverter.util.Resource

interface BaseRepository {

    suspend fun getRates(base: String): Resource<CurrencyResponse>
}