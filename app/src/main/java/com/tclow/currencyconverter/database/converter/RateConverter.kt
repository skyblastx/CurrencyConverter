package com.tclow.currencyconverter.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tclow.currencyconverter.database.dao.CurrencyRate

class RateConverter {
    @TypeConverter
    fun fromRatesList(rates: List<CurrencyRate>): String?{
        return if (rates == null){
            null
        } else {
            val gson = Gson()
            val type = object : TypeToken<CurrencyRate>(){

            }.type
            gson.toJson(rates, type)
        }
    }

    @TypeConverter fun toRatesList(rateString: String): List<CurrencyRate>? {
        if (rateString == null){
            return null
        } else {
            val gson = Gson()
            val type = object : TypeToken<CurrencyRate>(){

            }.type
            return gson.fromJson(rateString, type)
        }
    }
}