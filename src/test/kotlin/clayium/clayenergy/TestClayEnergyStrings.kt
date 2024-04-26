package clayium.clayenergy

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestClayEnergyStrings : StringSpec({
    // X" CE" is not included, they're defined in lang files.
    "test 0 CE" {
        ClayEnergy.ZERO.toString() shouldBe "0"
    }
    "micro 1: 10" {
        ClayEnergy.micro(10).toString() shouldBe "10u"
    }
    "micro 2: 240" {
        ClayEnergy.micro(240).toString() shouldBe "240u"
    }
    "milli: 13" {
        ClayEnergy.milli(13).toString() shouldBe "13m"
    }
    "57 CE" {
        ClayEnergy.of(57).toString() shouldBe "57"
    }
    "kilo: 96k CE" {
        ClayEnergy.of(96_000).toString() shouldBe "96k"
    }
    "mega: 38M CE" {
        ClayEnergy.of(38_000_000).toString() shouldBe "38M"
    }
    "giga: 77G CE" {
        ClayEnergy.of(77_000_000_000).toString() shouldBe "77G"
    }
    "tera: 53T CE" {
        ClayEnergy.of(53_000_000_000_000).toString() shouldBe "53T"
    }
})