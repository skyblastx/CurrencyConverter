package com.tclow.currencyconverter.data.model

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "Rates")
class Rates: Serializable {
    @PrimaryKey(autoGenerate = false)
    var currencyCode: String? = null

    @ColumnInfo(name = "rates")
    var rates: Int? = null
}