package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.api.W
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState

class LaserRecipe(
    val inputBlock: Block,
    val outputState: IBlockState,
    val inputMeta: Int = W,
    val energyMin: Double,
    val energyMax: Double,
    val requiredEnergy: Double
) {

    fun matches(input: IBlockState, energy: Double): Boolean {
        val sameBlock = inputBlock === input.block
        val metaMatches = inputMeta == W || inputMeta == input.block.getMetaFromState(input)
        val energyOk = energyMin <= energy && energyMax >= energy
        return sameBlock && metaMatches && energyOk
    }

    fun grsMatches(input: Block, meta: Int?): Boolean {
        return this.inputBlock === input && this.inputMeta == meta
    }
    fun isSufficient(totalEnergy: Double): Boolean {
        return requiredEnergy <= totalEnergy
    }
}