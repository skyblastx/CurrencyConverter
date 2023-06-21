package com.tclow.currencyconverter.database.dao

import androidx.room.*
import com.tclow.currencyconverter.data.model.CurrencyResponse

@Entity(tableName = "currencyrate")
class CurrencyRate {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "currencycode")
    var currencyCode: String? = null

    @ColumnInfo(name = "rates")
    var rates: Map<String, Double>? = null
}