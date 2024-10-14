package com.github.trc.clayium.clayenergy

import com.github.trc.clayium.api.ClayEnergy
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class TestClayEnergyStrings :
    StringSpec({
        "-10μ CE" { ClayEnergy.micro(-10).format() shouldBe "-10uCE" }
        "0 CE" { ClayEnergy.ZERO.format() shouldBe "0CE" }
        "10μ CE - minimum unit value" { ClayEnergy.micro(10).format() shouldBe "10uCE" }
        "13m CE - small value" { ClayEnergy.milli(13).format() shouldBe "13mCE" }
        "1 CE" { ClayEnergy.of(1).format() shouldBe "1CE" }
        "57 CE" { ClayEnergy.of(57).format() shouldBe "57CE" }
        "250 CE" { ClayEnergy.of(250).format() shouldBe "250CE" }
        "6k CE - kilo value" { ClayEnergy.of(6000).format() shouldBe "6kCE" }
        "500k CE" { ClayEnergy.of(500_000).format() shouldBe "500kCE" }
        "53T CE - big value" { ClayEnergy.of(53_000_000_000_000).format() shouldBe "53TCE" }
        "1.234k CE - decimal value" { ClayEnergy.of(1234).format() shouldBe "1.234kCE" }
    })
