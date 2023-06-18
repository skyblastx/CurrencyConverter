package com.tclow.currencyconverter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tclow.currencyconverter.data.model.Rates
import com.tclow.currencyconverter.database.converter.RateConverter
import com.tclow.currencyconverter.database.dao.RateDAO

@Database(entities = [Rates::class], version = 1, exportSchema = false)
@TypeConverters(RateConverter::class)
abstract class RateDatabase: RoomDatabase() {

    companion object{
        var rateDatabase: RateDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): RateDatabase {
            if (rateDatabase == null) {
                rateDatabase = Room.databaseBuilder(
                    context,
                    RateDatabase::class.java,
                    "rates.db"
                ).build()
            }
            return rateDatabase!!
        }
    }
    abstract fun rateDAO(): RateDAO
}