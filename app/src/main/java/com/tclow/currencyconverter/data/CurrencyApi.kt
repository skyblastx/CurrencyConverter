package com.tclow.currencyconverter.data

import com.tclow.currencyconverter.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val ACCESS_KEY = "c789c2ab33a432024bd7ff8938b92139"

interface CurrencyApi {

    @GET("/latest")
    suspend fun getRates(
        @Query("access_key") access_key: String = ACCESS_KEY
    ): Response<CurrencyResponse>

}