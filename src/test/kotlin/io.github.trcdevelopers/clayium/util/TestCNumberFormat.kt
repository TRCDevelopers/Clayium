package io.github.trcdevelopers.clayium.util

import io.github.trcdevelopers.clayium.common.util.CNumberFormat
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.RoundingMode

class TestCNumberFormat : FunSpec({

    lateinit var roundingModeDown: CNumberFormat
    lateinit var roundingModeHalfDown: CNumberFormat
    lateinit var lengthFixed: CNumberFormat

    beforeTest {
        roundingModeDown = CNumberFormat(CNumberFormat.Presets.default, RoundingMode.DOWN, "0.000")
        roundingModeHalfDown = CNumberFormat(CNumberFormat.Presets.default, RoundingMode.HALF_DOWN, "0.000")
        lengthFixed = CNumberFormat(CNumberFormat.Presets.default, RoundingMode.DOWN,
            decimalFormatPatternSupplier = { unit: CNumberFormat.DisplayUnit, displayValue: Double ->
                when (unit) {
                    CNumberFormat.DisplayUnit.NoUnit -> when {
                        displayValue < 10.0 -> "0.000" // 1.234
                        displayValue < 100.0 -> "0.00" // 12.34
                        else -> "0.0" // 123.4
                    }

                    CNumberFormat.DisplayUnit.ScientificNotation -> "0.00"
                    is CNumberFormat.DisplayUnit.Symbol -> when {
                        displayValue < 10.0 -> "0.00" // 1.23k
                        displayValue < 100.0 -> "0.0" //12.3k
                        else -> "0" // 123k
                    }

                }
            })
    }

    context("CNumberFormat Default") {
        withData(
            0.0 to "0.000",
            1.0 to "1.000",
            7.0 to "7.000",
            555.0 to "555.000",
            1000.0 to "1.000k",
            77777.0 to "77.777k",
            123_456_789.0 to "123.456M",
            555_123_456_789.0 to "555.123G",
            777_123_123_456_789.0 to "777.123T",
        ) { (value, formatted) ->
            val formatter = CNumberFormat.DEFAULT
            formatter.format(value) shouldBe formatted
        }
    }

    test("CNumberFormat RoundingMode") {
        roundingModeDown.format(9999.999) shouldBe "9.999k"
        roundingModeHalfDown.format(9999.999) shouldBe "10.000k"
        roundingModeHalfDown.format(9999.0) shouldBe "9.999k"
    }

    context("CNumberFormat lengthFixed") {
        withData(
            0.0 to "0.000",
            0.01234 to "12.3m",
            1.0 to "1.000",
            1234.5 to "1.23k",
        ) { (value, formatted) ->
            lengthFixed.format(value) shouldBe formatted
        }
    }

    context("CNumberFormat scientific notation") {
        withData(
            0.0 to "0.000",
            1.2345e-12 to "1.23E-12",
            0.01234 to "12.3m",
            1.0 to "1.000",
            1234.5 to "1.23k",
            1.2345e77 to "1.23E77",
        ) { (value, formatted) ->
            lengthFixed.format(value) shouldBe formatted
        }
    }
})