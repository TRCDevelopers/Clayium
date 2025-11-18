package io.github.trcdevelopers.clayium.api

import io.github.trcdevelopers.clayium.common.util.CNumberFormat
import net.minecraft.network.PacketBuffer

fun PacketBuffer.writeClayEnergy(energy: ClayEnergy) {
    writeLong(energy.energy)
}

fun PacketBuffer.readClayEnergy(): ClayEnergy {
    return ClayEnergy(readLong())
}

private val numberFormat = CNumberFormat.DEFAULT.copyToBuilder()
    .decimalFormatPatternProvider { unit: CNumberFormat.DisplayUnit, displayValue: Double ->
        if (displayValue == 0.0 || (unit is CNumberFormat.DisplayUnit.Symbol && unit.symbol == "u")) {
            "0"
        } else {
            "0.000"
        }
    }
    .build()

/**
 * @param energy 1 = 10uCE, 100_000 = 1CE.
 * You can use factory methods `ClayEnergy.of`, `.milli`, `micro` to create ClayEnergy.
 */
@JvmInline
value class ClayEnergy(val energy: Long) : Comparable<ClayEnergy> {

    val actualValue: Double get() = energy / 100_000.0

    fun formatWithTrailingZeros(): String {
        return this.formatWith(numberFormat)
    }

    fun format(): String {
        return this.formatWith(CNumberFormat.DEFAULT_NO_EXZERO)
    }

    fun formatWith(formatter: CNumberFormat): String {
        return "${formatter.format(actualValue)}CE"
    }

    override fun toString(): String {
        return "ClayEnergy(energy=$energy)"
    }

    operator fun plus(other: ClayEnergy) = ClayEnergy(energy + other.energy)
    operator fun minus(other: ClayEnergy) = ClayEnergy(energy - other.energy)
    operator fun times(value: Int) = ClayEnergy(energy * value)
    operator fun times(value: Long) = ClayEnergy(energy * value)
    operator fun times(value: Double) = ClayEnergy((energy * value).toLong())
    operator fun div(value: Int) = ClayEnergy(energy / value)
    operator fun div(value: Double) = ClayEnergy((energy.toDouble() / value).toLong())
    override operator fun compareTo(other: ClayEnergy) = energy.compareTo(other.energy)

    @Suppress("FunctionName")
    companion object {
        val ZERO = ClayEnergy(0)
        val MAX = ClayEnergy(Long.MAX_VALUE)

        val units = listOf("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")
        private val matchesExcessZero = Regex("0+\$")
        private val matchesExcessDecimalPoint = Regex("\\.$")

        fun micro(energy: Long): ClayEnergy {
            require(energy % 10 == 0.toLong()) {
                "10μ CE is a minimum unit of Clay Energy, but the given value is not a multiple of 10μ CE: $energy"
            }
            return ClayEnergy(energy / 10)
        }

        fun milli(energy: Long): ClayEnergy {
            return ClayEnergy(energy * 100)
        }

        fun of(energy: Long): ClayEnergy {
            return ClayEnergy(energy * 1000_00)
        }

        fun k(energy: Long): ClayEnergy {
            return ClayEnergy(energy * 1_000)
        }

        fun M(energy: Long): ClayEnergy {
            return ClayEnergy(energy * 1_000_000_000_00)
        }

        fun min(a: ClayEnergy, b: ClayEnergy) = if (a < b) a else b
        fun max(a: ClayEnergy, b: ClayEnergy) = if (a > b) a else b
    }
}