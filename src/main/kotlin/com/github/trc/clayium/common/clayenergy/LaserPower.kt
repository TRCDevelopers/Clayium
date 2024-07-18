package com.github.trc.clayium.common.clayenergy

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.pow

@JvmInline
value class LaserPower(val lp: Double) {

    // todo replace toString with this
    fun format(): String {
        if (lp == 0.0) return "0"
        val digits = floor(log(abs(lp),10.0)).toInt()
        val unitIndex = digits / 3
        val displayValue = String.format("%.3f", lp / 10.0.pow(unitIndex * 3))
            .replace(matchesExcessZero, "")
            .replace(matchesExcessDecimalPoint, "")
        return "$displayValue${units[unitIndex]}"
    }

    override fun toString(): String {
        return "ClayEnergy(energy=$lp)"
    }

    operator fun plus(other: LaserPower) = LaserPower(lp + other.lp)
    operator fun minus(other: LaserPower) = LaserPower(lp - other.lp)
    operator fun times(value: Int) = LaserPower(lp * value)
    operator fun times(value: Long) = LaserPower(lp * value)
    operator fun compareTo(other: LaserPower) = lp.compareTo(other.lp)

    companion object {
        val ZERO = LaserPower(0.0)

        val units = listOf("", "k", "M", "G", "T", "P", "E", "Z", "Y")
        private val matchesExcessZero = Regex("0+\$")
        private val matchesExcessDecimalPoint = Regex("\\.$")

        fun of(power: Double): LaserPower {
            return LaserPower(power)
        }
    }
}