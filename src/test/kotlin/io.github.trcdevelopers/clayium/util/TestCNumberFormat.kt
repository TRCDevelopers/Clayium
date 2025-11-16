package io.github.trcdevelopers.clayium.util

import io.github.trcdevelopers.clayium.common.util.CNumberFormat
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.RoundingMode

class TestCNumberFormat : FunSpec({

    lateinit var roundingModeDown: CNumberFormat
    lateinit var roundingModeHalfDown: CNumberFormat
    lateinit var lengthFixed: CNumberFormat

    beforeTest {
        roundingModeDown = CNumberFormat(CNumberFormat.Thresholds.default, CNumberFormat.Units.default, RoundingMode.DOWN, "0.000")
        roundingModeHalfDown = CNumberFormat(CNumberFormat.Thresholds.default, CNumberFormat.Units.default, RoundingMode.HALF_DOWN, "0.000")
        lengthFixed = CNumberFormat(CNumberFormat.Thresholds.default, CNumberFormat.Units.default, RoundingMode.DOWN, "0.000", true)
    }

    context("CNumberFormat Default") {
        withData(
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
            0.01234 to "12.3m",
            1.0 to "1.000",
            1234.5 to "1.23k",
        ) { (value, formatted) ->
            lengthFixed.format(value) shouldBe formatted
        }
    }

    context("CNumberFormat scientific notation") {
        withData(
            1.2345e-12 to "1.234E-12",
            0.01234 to "12.3m",
            1.0 to "1.000",
            1234.5 to "1.23k",
            1.2345e77 to "1.234E77",
        ) { (value, formatted) ->
            lengthFixed.format(value) shouldBe formatted
        }
    }
})