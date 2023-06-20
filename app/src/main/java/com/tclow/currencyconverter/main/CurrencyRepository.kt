package com.tclow.currencyconverter.main

import com.tclow.currencyconverter.data.CurrencyApi
import com.tclow.currencyconverter.data.model.CurrencyResponse
import com.tclow.currencyconverter.util.Resource
import java.lang.Exception
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private var api: CurrencyApi
) : BaseRepository {

    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}