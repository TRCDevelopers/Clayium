package com.github.trc.clayium.api

import net.minecraft.network.PacketBuffer
import kotlin.math.abs
import kotlin.math.pow
import kotlin.text.format

fun PacketBuffer.writeClayEnergy(energy: ClayEnergy) {
    writeLong(energy.energy)
}

fun PacketBuffer.readClayEnergy(): ClayEnergy {
    return ClayEnergy(readLong())
}

@JvmInline
value class ClayEnergy(val energy: Long) : Comparable<ClayEnergy> {

    fun format(): String {
        if (energy == 0L) return "0CE"
        val digits = abs(energy).toString().length
        val microCe = energy.toDouble() * 10.0
        val unitIndex = digits / 3
        val displayValue = String.format("%.03f", microCe / 10.0.pow(unitIndex * 3))
        return "$displayValue${units[unitIndex]}CE"
    }

    override fun toString(): String {
        return "ClayEnergy(energy=$energy)"
    }

    operator fun plus(other: ClayEnergy) = ClayEnergy(energy + other.energy)
    operator fun minus(other: ClayEnergy) = ClayEnergy(energy - other.energy)
    operator fun times(value: Int) = ClayEnergy(energy * value)
    operator fun times(value: Long) = ClayEnergy(energy * value)
    operator fun div(value: Int) = ClayEnergy(energy / value)
    override operator fun compareTo(other: ClayEnergy) = energy.compareTo(other.energy)

    companion object {
        val ZERO = ClayEnergy(0)
        val MAX = ClayEnergy(Long.MAX_VALUE)

        val units = listOf("u", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")

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
    }
}