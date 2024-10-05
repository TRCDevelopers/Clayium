package com.github.trc.clayium.common.recipe

import net.minecraft.block.Block

class LaserRecipe(
    val input: Block,
    val output: Block,
    val energyMin: Double,
    val energyMax: Double?,
    val requiredEnergy: Double
) {

    fun matches(input: Block, energy: Double): Boolean {
        return this.input === input && energyMin <= energy && if (energyMax != null) energyMax >= energy else true
    }

    fun isSufficient(totalEnergy: Double): Boolean {
        return requiredEnergy <= totalEnergy
    }
}