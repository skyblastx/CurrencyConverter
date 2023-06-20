package com.tclow.currencyconverter.data

import com.tclow.currencyconverter.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CurrencyApi {

    @GET("/latest")
    suspend fun getRates(
        @QueryMap options: Map<String, String>
    ): Response<CurrencyResponse>
}