package com.tclow.currencyconverter.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RateDAO {
    @Query("SELECT * FROM currencyrate ORDER BY date DESC")
    suspend fun getAllRates(): List<CurrencyRate>

    @Query("SELECT * FROM currencyrate WHERE date = :date")
    suspend fun getRatesWithDate(date: String): List<CurrencyRate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(category: CurrencyRate)
}