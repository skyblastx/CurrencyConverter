package com.tclow.currencyconverter.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RateDAO {

    @Query("SELECT * FROM currencyrate ORDER BY currencyCode DESC")
    suspend fun getAllRates(): List<CurrencyRate>

    @Query("SELECT * FROM currencyrate WHERE date = :date")
    suspend fun getRatesWithDate(date: String): List<CurrencyRate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(category: CurrencyRate)

    @Query("DELETE FROM currencyrate")
    suspend fun clearDb()

    @Query("DELETE FROM currencyrate WHERE date = :date")
    suspend fun deleteSpecificDate(date: String)

    @Query("WITH date_range AS " +
            "(SELECT :startDate AS date " +
            "UNION ALL SELECT strftime('%Y-%m-%d', date(date, '+1 day')) AS date " +
            "FROM date_range WHERE date < :endDate)" +
            "SELECT date FROM date_range " +
            "WHERE date NOT IN (SELECT DISTINCT date FROM currencyrate) ORDER BY date")
    suspend fun getMissingDates(startDate: String, endDate: String): List<String>
}