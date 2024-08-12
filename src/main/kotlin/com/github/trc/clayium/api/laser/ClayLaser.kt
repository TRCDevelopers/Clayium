package com.github.trc.clayium.api.laser

import net.minecraft.util.EnumFacing
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

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
            val C_i = b_i.pow((r1) * (ln(r1 / r) / ln(m_i)))
            val ai_top = ln(r1 / (C_i.pow(-nd) + r))
            val ai_bottom = ln(r1 / r)
            val ai = ai_top / ai_bottom

            val E_i = m_i.pow(ai) * ((1 + r * n_i * C_i.pow(nd)) / (1 + r * C_i.pow(nd)))
            return max(1.0, E_i)
        }

        fun fromInt(color: Int, direction: EnumFacing): ClayLaser {
            val red = color shr 16 and 0xFF
            val green = color shr 8 and 0xFF
            val blue = color and 0xFF
            return ClayLaser(direction, red, green, blue)
        }
    }
}