package com.tclow.currencyconverter.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HashMapConverter {

    @TypeConverter
    fun toHashMap(value: String): Map<String, Double> =
        Gson().fromJson(value, object : TypeToken<Map<String, Double>>() {}.type)

    @TypeConverter
    fun fromHashMap(value: Map<String, Double>): String =
        Gson().toJson(value)
}