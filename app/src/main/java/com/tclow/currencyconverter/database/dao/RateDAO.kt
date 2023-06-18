package com.tclow.currencyconverter.database.dao

import androidx.room.Query
import com.tclow.currencyconverter.data.model.Rates

interface RateDAO {

    @Query("SELECT * FROM rates ORDER BY currencyCode DESC")
    suspend fun getAllRates(): List<Rates>
}