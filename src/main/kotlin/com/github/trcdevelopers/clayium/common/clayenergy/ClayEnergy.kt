package com.github.trcdevelopers.clayium.common.clayenergy

import com.github.trcdevelopers.clayium.common.util.UtilLocale

@JvmInline
value class ClayEnergy(val energy: Long) {

    override fun toString(): String {
        return UtilLocale.ClayEnergyNumeral(energy.toDouble())
    }

    operator fun plus(other: ClayEnergy) = ClayEnergy(energy + other.energy)
    operator fun minus(other: ClayEnergy) = ClayEnergy(energy - other.energy)
    operator fun times(value: Int) = ClayEnergy(energy * value)
    operator fun compareTo(other: ClayEnergy) = energy.compareTo(other.energy)

    companion object {
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