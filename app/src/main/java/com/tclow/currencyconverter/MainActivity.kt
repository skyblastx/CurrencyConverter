package com.tclow.currencyconverter

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import com.tclow.currencyconverter.database.RateDatabase
import com.tclow.currencyconverter.databinding.ActivityMainBinding
import com.tclow.currencyconverter.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate


@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpButtonListeners()

        lifecycleScope.launchWhenStarted {
            // Get rates immediately
            if (RateDatabase.getDatabase(this@MainActivity).rateDAO().getRatesWithDate(LocalDate.now().toString()).isEmpty())
            {
                viewModel.getRates(this@MainActivity)
                binding.mainLblTodayRates.text = "Today's rates has been updated."
            }

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
            viewModel.convert(
                this,
                binding.mainAmt.text.toString(),
                binding.mainBtnFrom.text.toString(),
                binding.mainBtnTo.text.toString()
            )
        }
    }
}