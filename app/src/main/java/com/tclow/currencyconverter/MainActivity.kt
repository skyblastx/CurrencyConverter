package com.tclow.currencyconverter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import com.tclow.currencyconverter.database.RateDatabase
import com.tclow.currencyconverter.databinding.ActivityMainBinding
import com.tclow.currencyconverter.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.chart
import io.data2viz.charts.chart.constant
import io.data2viz.charts.chart.discrete
import io.data2viz.charts.chart.mark.MarkCurves
import io.data2viz.charts.chart.mark.line
import io.data2viz.charts.chart.mark.lineMark
import io.data2viz.charts.chart.quantitative
import io.data2viz.charts.core.CursorType
import io.data2viz.geom.Size
import io.data2viz.shape.Symbols
import io.data2viz.viz.VizContainerView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val todayDate = LocalDate.now()
    private val oneWeekAgoDate = todayDate.minusDays(6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpButtonListeners()

        lifecycleScope.launchWhenStarted {

            // Get rates for missing dates in the past week when app launches
            val missingDates = RateDatabase.getDatabase(this@MainActivity).rateDAO().getMissingDates(oneWeekAgoDate.toString(), todayDate.toString())

            for (date in missingDates)
            {
                viewModel.getRatesWithDate(this@MainActivity, date)
            }

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.conversion.collect { event ->
                        when (event) {
                            is MainViewModel.CurrencyEvent.Success -> {
                                binding.progressBar.isVisible = false
                                binding.mainTxtResult.setTextColor(Color.BLACK)
                                binding.mainTxtResult.text = event.resultText
                            }
                            is MainViewModel.CurrencyEvent.Failure -> {
                                binding.progressBar.isVisible = false
                                binding.mainTxtResult.setTextColor(Color.RED)
                                binding.mainTxtResult.text = event.errorText
                            }
                            is MainViewModel.CurrencyEvent.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.chartConversion.collect { event ->
                        when (event) {
                            is MainViewModel.ChartEvent.Success -> {
                                binding.progressBar.isVisible = false

                                // Reset previous data
                                canPop.clear()
                                binding.mainChartView.removeAllViews()

                                // Assign chart value based on response
                                for (result in event.result) {
                                    for ((date, rate) in result) {
                                        val formattedDate = formatDate(date)
                                        val temp = PopCount(formattedDate, rate)
                                        canPop.add(temp)
                                    }
                                }

                                // Add new chart view
                                binding.mainChartView.addView(CurrencyRateChart(
                                    this@MainActivity,
                                    binding.mainBtnTo.text as String
                                ))
                                binding.mainChartView.isVisible = true
                            }
                            is MainViewModel.ChartEvent.Failure -> {
                                binding.progressBar.isVisible = false
                            }
                            is MainViewModel.ChartEvent.Loading -> {
                                binding.progressBar.isVisible = true
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setUpButtonListeners() {
        binding.mainBtnFrom.setOnClickListener {
            launchCountryPickerDialog { selectedCountry: CPCountry? ->
                binding.mainBtnFrom.text = selectedCountry?.currencyCode
            }
        }

        binding.mainBtnTo.setOnClickListener {
            launchCountryPickerDialog { selectedCountry: CPCountry? ->
                binding.mainBtnTo.text = selectedCountry?.currencyCode
            }
        }

        binding.btnConvert.setOnClickListener {
            // Get rates for today and convert it
            viewModel.convert(
                this,
                binding.mainAmt.text.toString(),
                binding.mainBtnFrom.text.toString(),
                binding.mainBtnTo.text.toString()
            )

            // Get rates for past week and show as chart
            viewModel.createChart(
                this,
                binding.mainBtnTo.text.toString()
            )
        }
    }

    private fun formatDate(date: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormat = DateTimeFormatter.ofPattern("MM/dd")
        val tempDate = LocalDate.parse(date, inputFormat)

        return tempDate.format(outputFormat)
    }
}

class CurrencyRateChart(context: Context, base: String) : VizContainerView(context) {

    private val chart: Chart<PopCount> = chart(canPop) {
        size = Size(vizSize, vizSize)
        title = "Currency Rate of Euro to $base for the past week"

        // Create a discrete dimension for dates
        val date = discrete({ domain.date })

        // Create a continuous numeric dimension for the rates
        val rates = quantitative({ domain.rates })

        // Using a discrete dimension for the X-axis and a continuous one for the Y-axis
        line(date, rates) {
            marker = constant(Symbols.Circle)
            showMarkers = true
            strokeWidth = constant(2.0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        chart.size = Size(vizSize, vizSize * h / w)
    }
}

const val vizSize = 500.0

data class PopCount(val date: String, val rates: Double)

val canPop = mutableListOf<PopCount>()