package com.tclow.currencyconverter.main

import com.tclow.currencyconverter.data.CurrencyApi
import com.tclow.currencyconverter.data.model.CurrencyResponse
import com.tclow.currencyconverter.util.Resource
import java.lang.Exception
import javax.inject.Inject

private const val ACCESS_KEY = "c789c2ab33a432024bd7ff8938b92139"
class CurrencyRepository @Inject constructor(
    private var api: CurrencyApi
) : BaseRepository {
    override suspend fun getRates(): Resource<CurrencyResponse> {
        return try {
            val data = getDataMapForQuery(null)
            val response = api.getRates(data)
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

    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val data = getDataMapForQuery(base)
            val response = api.getRates(data)
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

    override suspend fun getHistoricalRates(date: String): Resource<CurrencyResponse> {
        return try {
            val data = getDataMapForQuery(null)
            val response = api.getHistoricalRates(date, data)
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

    private fun getDataMapForQuery(base: String?): Map<String, String> {
        val data = mutableMapOf<String, String>()

        data["access_key"] = ACCESS_KEY
        if (!base.isNullOrEmpty()) { data["base"] = base }

        return data
    }
}