package com.github.trc.clayium.api

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.pow

@JvmInline
value class LaserEnergy(val energy: Double) {

    fun format(): String {
        if (energy == 0.0) return "0"
        val digits = floor(log(abs(energy), 10.0)).toInt()
        val unitIndex = digits / 3
        val displayValue = String.format("%.0f", energy / 10.0.pow(unitIndex * 3))
        return "$displayValue${units[unitIndex]}"
    }

    override fun toString(): String {
        return "LaserEnergy(energy=$energy)"
    }

    operator fun plus(other: LaserEnergy) = LaserEnergy(energy + other.energy)

    operator fun minus(other: LaserEnergy) = LaserEnergy(energy - other.energy)

    operator fun times(value: Int) = LaserEnergy(energy * value)

    operator fun times(value: Long) = LaserEnergy(energy * value)

    operator fun compareTo(other: LaserEnergy) = energy.compareTo(other.energy)

    companion object {
        val ZERO = LaserEnergy(0.0)

        val units = listOf("", "k", "M", "G", "T", "P", "E", "Z", "Y")
    }
}
