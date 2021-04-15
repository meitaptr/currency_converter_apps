package com.example.currency_converter_metranettest

import org.jetbrains.annotations.TestOnly

object Calculate {

    fun calculateAmount(currency1: Any, currency2: Any, amount: Any): String =
        (amount.toString().toDouble() * currency2.toString().toDouble() / currency1.toString().toDouble()).toString()
}