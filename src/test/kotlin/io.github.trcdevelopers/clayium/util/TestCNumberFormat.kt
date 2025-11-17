package io.github.trcdevelopers.clayium.util

import io.github.trcdevelopers.clayium.common.util.CNumberFormat
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.RoundingMode

class TestCNumberFormat : FunSpec({

    lateinit var default: CNumberFormat
    lateinit var roundingModeUp: CNumberFormat
    lateinit var lengthFixed: CNumberFormat

    beforeTest {
        default = CNumberFormat.DEFAULT
        roundingModeUp = CNumberFormat.DEFAULT.copyToBuilder().roundingMode(RoundingMode.UP).build()
        lengthFixed = CNumberFormat.DEFAULT.copyToBuilder().maxLength(4).build()
    }

    context("Mostly works") {
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

    test("Default RoundingMode is Down") {
        default.format(9999.999) shouldBe "9.999k"
    }

    test("RoundingMode Takes Effect") {
        roundingModeUp.format(9999.999) shouldBe "10.000k"
    }

    context("Max Length Works") {
        withData(
            0.0 to "0.000",
            0.01234 to "12.3m",
            1.0 to "1.000",
            1234.5 to "1.23k",
            9999.9999 to "9.99k",
        ) { (value, formatted) ->
            lengthFixed.format(value) shouldBe formatted
        }
    }

    context("Scientific Notation is used if too small/big") {
        withData(
            1.2345e-12 to "1.234E-12",
            1.2345e77 to "1.234E77",
        ) { (value, formatted) ->
            default.format(value) shouldBe formatted
        }
    }
})