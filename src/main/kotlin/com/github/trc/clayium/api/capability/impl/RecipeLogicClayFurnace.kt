package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.config.ConfigTierBalance
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes

/**
 * This logic can process vanilla furnace recipes in addition to the recipes in the recipe registry.
 * Speed and energy consumption increase with the tier of the machine.
 */
class RecipeLogicClayFurnace(
    metaTileEntity: MetaTileEntity,
    registry: RecipeRegistry<*>,
    clayEnergyHolder: ClayEnergyHolder,
) : RecipeLogicEnergy(metaTileEntity, registry, clayEnergyHolder) {

    override fun trySearchNewRecipe() {
        val smeltingResult = FurnaceRecipes.instance().getSmeltingResult(inputInventory.getStackInSlot(0)).copy()
        if (smeltingResult.isEmpty) return super.trySearchNewRecipe()
        prepareVanillaFurnaceRecipe(smeltingResult)
    }

    override fun applyOverclock(cePt: ClayEnergy, duration: Long, compensatedFactor: Double): LongArray {
        val (cet, duration) = super.applyOverclock(cePt, duration, compensatedFactor)
        val machineTierNum = metaTileEntity.tier.numeric
        val multipliedRecipeCEt =
            ClayEnergy((cet.toDouble() * ConfigTierBalance.crafting.smelterConsumingEnergyMultiplier[machineTierNum - 4]).toLong())
        val multipliedRecipeTime = (duration * ConfigTierBalance.crafting.smelterCraftTimeMultiplier[machineTierNum - 4]).toLong()
        return longArrayOf(multipliedRecipeCEt.energy, multipliedRecipeTime)
    }

    override fun prepareRecipe(recipe: Recipe) {
        val multipliedRecipeCEt = ClayEnergy(
            (recipe.cePerTick.energy.toDouble() * ConfigTierBalance.crafting.smelterConsumingEnergyMultiplier[metaTileEntity.tier.numeric - 4]).toLong()
        )
        val multipliedRecipeTime = (
            recipe.duration * ConfigTierBalance.crafting.smelterCraftTimeMultiplier[metaTileEntity.tier.numeric - 4]
        ).toLong()
        if (!this.drawEnergy(multipliedRecipeCEt, simulate = true)) return
        val outputs = recipe.copyOutputs()
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
            this.outputsFull = true
            return
        }
        if (!recipe.matches(true, inputInventory, tierNum)) return
        this.itemOutputs = outputs
        this.recipeCEt = multipliedRecipeCEt
        this.requiredProgress = multipliedRecipeTime
        this.currentProgress = 1
        this.previousRecipe = recipe
    }

    private fun prepareVanillaFurnaceRecipe(smeltingResult: ItemStack) {
        require(!smeltingResult.isEmpty)
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, listOf(smeltingResult), true)) {
            this.outputsFull = true
            return
        }
        this.inputInventory.extractItem(0, 1, false)

        val (cet, duration) = applyOverclock(BASE_CE_CONSUMPTION, FURNACE_RECIPE_TIME, ocHandler.compensatedFactor)
        this.itemOutputs = listOf(smeltingResult)
        this.recipeCEt = ClayEnergy(cet)
        this.requiredProgress = duration
        this.currentProgress = 1
    }

    private companion object {
        private const val FURNACE_RECIPE_TIME = 200L //ticks
        private val BASE_CE_CONSUMPTION = ClayEnergy(4)
    }
}