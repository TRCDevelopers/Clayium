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

    override val laserEnergy: Double = calcLaserEnergyPerColor(laserBlue, bases[0], maxEnergies[0], dampingRates[0]) *
                                       calcLaserEnergyPerColor(laserGreen, bases[1], maxEnergies[1], dampingRates[1]) *
                                       calcLaserEnergyPerColor(laserRed, bases[2], maxEnergies[2], dampingRates[2]) - 1

    fun changeDirection(direction: EnumFacing): ClayLaser {
        return ClayLaser(direction, laserRed, laserGreen, laserBlue)
    }

    companion object {
        private val bases = doubleArrayOf(2.5, 1.8, 1.5)
        private val maxEnergies = doubleArrayOf(1000.0, 300.0, 100.0)
        private val dampingRates = doubleArrayOf(0.1, 0.1, 0.1)

        /**
         * the calculation is found [here](https://clayium.wiki.fc2.com/wiki/%E3%83%AC%E3%83%BC%E3%82%B6%E3%83%BC)
         * todo: wikiと計算結果が異なるので実際の動作確認が必要
         */
        private fun calcLaserEnergyPerColor(laserStrength: Int, base: Double, maxEnergy: Double, r: Double): Double {
            if (laserStrength <= 0 || r <= 0.0 || maxEnergy < 0.0 || base  < 1.0) return 1.0
            val laserStrengthd = laserStrength.toDouble()
            val c = Math.log(maxEnergy) / Math.log((1.0 + r) / r) * Math.log(base)
            val a = Math.pow(Math.E, (1.0 + r) / c)
            val e = Math.pow(maxEnergy, Math.log((1.0 + r) / (Math.pow(a, -laserStrengthd) + r)) / Math.log((1.0 + r) / r))
            val m = 1.0 / (1.0 + r * Math.pow(a, laserStrengthd)) + r * Math.pow(a, laserStrengthd) / (1.0 + r * Math.pow(a, laserStrengthd)) * laserStrengthd
            return max(e * m, 1.0)
        }
    }
}