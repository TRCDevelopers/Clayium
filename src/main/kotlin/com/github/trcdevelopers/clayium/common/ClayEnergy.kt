package com.github.trcdevelopers.clayium.common

@JvmInline
value class ClayEnergy private constructor(private val energy: Long) {
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