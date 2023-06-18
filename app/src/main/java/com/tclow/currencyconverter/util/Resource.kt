package com.tclow.currencyconverter.util

/**
 * wrapper for [CurrencyResponse] to handle errors of network request
 */
sealed class Resource<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T>(message: String) : Resource<T>(null, message)
}