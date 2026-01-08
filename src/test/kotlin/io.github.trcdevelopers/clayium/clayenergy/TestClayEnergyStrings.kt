package io.github.trcdevelopers.clayium.clayenergy

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class TestClayEnergyStrings : StringSpec({
    "-10μ CE" {
        ClayEnergy.micro(-10).formatWithTrailingZerosAndUnit() shouldBe "-10uCE"
    }
    "0 CE" {
        ClayEnergy.ZERO.formatWithTrailingZerosAndUnit() shouldBe "0CE"
    }
    "10μ CE - minimum unit value" {
        ClayEnergy.micro(10).formatWithTrailingZerosAndUnit() shouldBe "10uCE"
    }
    "13m CE - small value" {
        ClayEnergy.milli(13).formatWithTrailingZerosAndUnit() shouldBe "13.000mCE"
    }
    "1 CE" {
        ClayEnergy.of(1).formatWithTrailingZerosAndUnit() shouldBe "1.000CE"
    }
    "57 CE" {
        ClayEnergy.of(57).formatWithTrailingZerosAndUnit() shouldBe "57.000CE"
    }
    "250 CE" {
        ClayEnergy.of(250).formatWithTrailingZerosAndUnit() shouldBe "250.000CE"
    }
    "6k CE - kilo value" {
        ClayEnergy.of(6789).formatWithTrailingZerosAndUnit() shouldBe "6.789kCE"
    }
    "500k CE" {
        ClayEnergy.of(500_000).formatWithTrailingZerosAndUnit() shouldBe "500.000kCE"
    }
    "53T CE - big value" {
        ClayEnergy.of(53_000_000_000_000).formatWithTrailingZerosAndUnit() shouldBe "53.000TCE"
    }
    "1.234k CE - decimal value" {
        ClayEnergy.of(1234).formatWithTrailingZerosAndUnit() shouldBe "1.234kCE"
    }

    "999 CE" {
        ClayEnergy.of(999).formatWithTrailingZerosAndUnit() shouldBe "999.000CE"
    }
})