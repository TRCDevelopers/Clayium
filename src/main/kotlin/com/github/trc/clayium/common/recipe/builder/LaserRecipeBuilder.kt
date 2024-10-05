package com.github.trc.clayium.common.recipe.builder

import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.common.recipe.LaserRecipe
import net.minecraft.block.Block
import net.minecraft.init.Blocks

class LaserRecipeBuilder{

    var input: Block? = null
    var output: Block? = null
    var energyMin: Double = 0.0
    var energyMax: Double? = null
    var requiredEnergy: Double = 0.0
    fun input(block: Block): LaserRecipeBuilder {
        input = block
        return this
    }

    fun output(block: Block): LaserRecipeBuilder {
        output = block
        return this
    }

    fun energyMin(value: Double): LaserRecipeBuilder {
        energyMin = value
        return this
    }

    fun energyMax(value: Double): LaserRecipeBuilder {
        energyMax = value
        return this
    }

    fun requiredEnergy(value: Double): LaserRecipeBuilder {
        requiredEnergy = value
        return this
    }
    fun build(): LaserRecipe {
        if (input == null){
            CLog.error("input must be set")
            input = Blocks.BARRIER
        }
        if (output == null){
            CLog.error("output must be set")
            output = Blocks.BARRIER
        }
        return LaserRecipe(input!!, output!!, energyMin, energyMax, requiredEnergy)
    }
}