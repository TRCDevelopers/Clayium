package io.github.trcdevelopers.clayium.util

import io.github.trcdevelopers.clayium.common.util.CNumberFormat
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class TestCNumberFormat : StringSpec({
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
})