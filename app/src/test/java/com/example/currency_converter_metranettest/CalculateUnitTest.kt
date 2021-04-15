package com.example.currency_converter_metranettest

import org.junit.Assert
import org.junit.Test

class CalculateUnitTest {

    @Test
    fun calculateAmount_isCorrect() {
        val usd = 1.19575
        val idr = 17476.787497
        val amount = 1
        val expected = "14615.753708551118"
        val actual = Calculate.calculateAmount(usd, idr, amount)
        Assert.assertEquals(expected, actual)
    }
}