package com.github.trcdevelopers.clayium.api.laser

import net.minecraft.util.EnumFacing
import kotlin.math.max

data class ClayLaser(
    override val laserDirection: EnumFacing,
    override val laserRed: Int,
    override val laserGreen: Int,
    override val laserBlue: Int,
    override val laserAge: Int = 0,
) : IClayLaser {

    override val laserEnergy: Double = calcLaserEnergyPerColor(laserBlue, bases[0], maxEnergies[0]) *
                                       calcLaserEnergyPerColor(laserGreen, bases[1], maxEnergies[1]) *
                                       calcLaserEnergyPerColor(laserRed, bases[2], maxEnergies[2]) - 1

    fun changeDirection(direction: EnumFacing): ClayLaser {
        return ClayLaser(direction, laserRed, laserGreen, laserBlue)
    }

    companion object {
        private val bases = doubleArrayOf(2.5, 1.8, 1.5)
        private val maxEnergies = doubleArrayOf(1000.0, 300.0, 100.0)

        @Suppress("LocalVariableName")
        private fun calcLaserEnergyPerColor(n_i: Int, b_i: Double, m_i: Double, r: Double = 0.1): Double {
            if (n_i <= 0 || r <= 0.0 || m_i < 0.0 || b_i  < 1.0) return 1.0
            val nd = n_i.toDouble()
            val r1 = r + 1.0
            val C_i = Math.pow(b_i, (r1) * (Math.log(r1 / r) / Math.log(m_i)))
            val ai_top = Math.log(r1 / (Math.pow(C_i, -nd) + r))
            val ai_bottom = Math.log(r1 / r)
            val ai = ai_top / ai_bottom

            val E_i = Math.pow(m_i, ai) * ((1 + r * n_i * Math.pow(C_i, nd)) / (1 + r * Math.pow(C_i, nd)))
            return max(1.0, E_i)
        }
    }
}