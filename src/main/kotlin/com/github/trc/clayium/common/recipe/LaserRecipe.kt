package com.github.trc.clayium.common.recipe

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraftforge.oredict.OreDictionary

class LaserRecipe(
    val input: Block,
    val output: Block,
    val inputMeta: Int?,
    val outputMeta: Int?,
    val energyMin: Double,
    val energyMax: Double,
    val requiredEnergy: Double
) {

    fun matches(input: IBlockState, energy: Double): Boolean {
        return this.input === input.block && if (this.inputMeta != null) this.inputMeta == input.block.getMetaFromState(input) else true && energyMin <= energy && energyMax >= energy
    }

    fun isSufficient(totalEnergy: Double): Boolean {
        return requiredEnergy <= totalEnergy
    }
}