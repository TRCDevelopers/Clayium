package com.github.trc.clayium.common.recipe.builder

import com.github.trc.clayium.api.W
import com.github.trc.clayium.common.recipe.LaserRecipe
import com.github.trc.clayium.common.recipe.registry.LaserRecipeRegistry
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState

class LaserRecipeBuilder(
    private val registry: LaserRecipeRegistry,
) {

    lateinit var input: Block
    lateinit var output: IBlockState
    var energyMin: Double = 0.0
    var energyMax: Double = Double.MAX_VALUE
    var requiredEnergy: Double = 0.0
    var inputMeta: Int = W

    /**
     * add input as block + meta pair.
     * matches block and meta.
     */
    fun input(block: Block, meta: Int): LaserRecipeBuilder {
        input = block
        inputMeta = meta
        println("$input $inputMeta")
        return this
    }

    /**
     * add input as block + meta pair.
     * matches block and meta.
     */
    fun input(state: IBlockState) = input(state.block, state.block.getMetaFromState(state))

    /**
     * add input as block.
     * matches block only, ignores meta.
     * i.e. meta is treated as wildcard.
     */
    fun input(block: Block) = input(block, W)

    fun output(state: IBlockState): LaserRecipeBuilder {
        output = state
        return this
    }
    fun output(block: Block, meta: Int): LaserRecipeBuilder {
        @Suppress("DEPRECATION")
        output = block.getStateFromMeta(meta)
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

    fun buildAndRegister() {
        val recipe = LaserRecipe(
            inputBlock = input, inputMeta = inputMeta, outputState = output,
            energyMin = energyMin, energyMax = energyMax, requiredEnergy = requiredEnergy
        )
        registry.register(recipe)
    }
}