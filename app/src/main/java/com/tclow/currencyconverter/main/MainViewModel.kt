package com.tclow.currencyconverter.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tclow.currencyconverter.database.RateDatabase
import com.tclow.currencyconverter.database.dao.CurrencyRate
import com.tclow.currencyconverter.util.DispatcherProvider
import com.tclow.currencyconverter.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: BaseRepository,
    private val dispatchers: DispatcherProvider
): ViewModel() {
    sealed class CurrencyEvent {
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Loading: CurrencyEvent()
        object Empty: CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun getRates(context: Context)
    {
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            when (val ratesResponse = repository.getRates()) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    // TODO: Store response into db
                    val rspRate = CurrencyRate().apply {
                        this.rates = ratesResponse.data?.rates
                        this.date = ratesResponse.data?.date
                        this.currencyCode = ratesResponse.data?.base
                    }

                    context.let {
                        RateDatabase.getDatabase(it).rateDAO().insertRates(rspRate)
                        _conversion.value = CurrencyEvent.Success("") // Send in empty string will do
                    }
                }
            }
        }
    }

    fun getRatesWithDate(context: Context, date: String) {
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            when (val ratesResponse = repository.getHistoricalRates(date)) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    // TODO: Store response into db
                    val rspRate = CurrencyRate().apply {
                        this.rates = ratesResponse.data?.rates
                        this.date = ratesResponse.data?.date
                        this.currencyCode = ratesResponse.data?.base
                    }

                    context.let {
                        RateDatabase.getDatabase(it).rateDAO().insertRates(rspRate)
                        _conversion.value = CurrencyEvent.Success("") // Send in empty string will do
                    }
                }
            }
        }
    }

    fun convert(
        context: Context,
        amountStr: String,
        fromCurrency: String,
        toCurrency: String
    ) {
        val fromAmount = amountStr.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading

            val ratesResponse = RateDatabase.getDatabase(context).rateDAO().getRatesWithDate(LocalDate.now().toString())
            val rates: Map<String, Double>
            if (ratesResponse.isNotEmpty())
            {
                rates = ratesResponse[0].rates!!

                val rate = getRateForCurrency(toCurrency, rates)
                if (rate == null) {
                    _conversion.value = CurrencyEvent.Failure("Unexpected error")
                } else {
                    val convertedCurrency = round(fromAmount * rate * 100) / 100
                    _conversion.value = CurrencyEvent.Success("$fromAmount $fromCurrency = $convertedCurrency $toCurrency")
                }
            }
            else
            {
                // If database returned empty value, return failed event
                _conversion.value = CurrencyEvent.Failure("Failed to access database.")
            }
        }
    }

    private fun getRateForCurrency(requestCurrency: String, rates: Map<String, Double>): Double? {
        var result: Double? = null

        for ((currency, rate) in rates) {
            if (requestCurrency == currency) {
                result = rate
                break
            }
        }

        return result
    }

    // ============================================================================================
    // Chart Events
    // ============================================================================================

    sealed class ChartEvent {
        class Success(val result: List<Map<String, Double>>): ChartEvent()
        class Failure(): ChartEvent()

        object Loading: ChartEvent()
        object Empty: ChartEvent()
    }

    private val _chartConversion = MutableStateFlow<ChartEvent>(ChartEvent.Empty)
    val chartConversion: StateFlow<ChartEvent> = _chartConversion

    fun createChart(context: Context, base: String) {

        viewModelScope.launch(dispatchers.io) {
            _chartConversion.value = ChartEvent.Loading
            val datesInWeek = mutableListOf<String>()
            val result = mutableListOf<Map<String, Double>>()

            for (i in 6 downTo 0) {
                datesInWeek.add(LocalDate.now().minusDays(i.toLong()).toString())
            }

            for (date in datesInWeek) {
                val ratesResponse = RateDatabase.getDatabase(context).rateDAO().getRatesWithDate(date)
                val rates: Map<String, Double>
                if (ratesResponse.isNotEmpty())
                {
                    rates = ratesResponse[0].rates!!
                    val specificRate = getRateForCurrency(base, rates)

                    if (specificRate != null) {
                        val tempMap = mutableMapOf<String, Double>()
                        tempMap[date] = specificRate
                        result.add(tempMap)
                    }
                    else {
                        _chartConversion.value = ChartEvent.Failure()
                    }
                }
                else
                {
                    // If database returned empty value, return failed event
                    _chartConversion.value = ChartEvent.Failure()
                }
            }

            _chartConversion.value = ChartEvent.Success(result)
        }
    }
}