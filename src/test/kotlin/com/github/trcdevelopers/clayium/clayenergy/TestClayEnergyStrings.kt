package com.github.trcdevelopers.clayium.clayenergy

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class TestClayEnergyStrings : StringSpec({
    "-10μ CE" {
        ClayEnergy.micro(-10).format() shouldBe "-10μ CE"
    }
    "0 CE" {
        ClayEnergy.ZERO.format() shouldBe "0 CE"
    }
    "10μ CE - minimum unit value" {
        ClayEnergy.micro(10).format() shouldBe "10μ CE"
    }
    "13m CE - small value" {
        ClayEnergy.milli(13).format() shouldBe "13m CE"
    }
    "57 CE" {
        ClayEnergy.of(57).format() shouldBe "57 CE"
    }
    "6k CE - kilo value" {
        ClayEnergy.of(6000).format() shouldBe "6k CE"
    }
    "53T CE - big value" {
        ClayEnergy.of(53_000_000_000_000).format() shouldBe "53T CE"
    }
    "1.234k CE - decimal value" {
        ClayEnergy.of(1234).format() shouldBe "1.234k CE"
    }
})