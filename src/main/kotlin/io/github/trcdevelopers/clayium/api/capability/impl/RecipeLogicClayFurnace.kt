package io.github.trcdevelopers.clayium.api.capability.impl

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import io.github.trcdevelopers.clayium.common.recipe.Recipe
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import io.github.trcdevelopers.clayium.common.util.TransferUtils
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

    override fun trySearchNewRecipe(): Boolean {
        val smeltingResult = FurnaceRecipes.instance().getSmeltingResult(inputInventory.getStackInSlot(0)).copy()
        if (smeltingResult.isEmpty) return super.trySearchNewRecipe()
        return prepareVanillaFurnaceRecipe(smeltingResult)
    }

    override fun applyOverclock(cePt: ClayEnergy, duration: Long, compensatedFactor: Double): LongArray {
        val (cet, duration) = super.applyOverclock(cePt, duration, compensatedFactor)
        val machineTierNum = metaTileEntity.tier.numeric
        val multipliedRecipeCEt =
            ClayEnergy((cet.toDouble() * ConfigTierBalance.crafting.smelterConsumingEnergyMultiplier[machineTierNum - 4]).toLong())
        val multipliedRecipeTime = (duration * ConfigTierBalance.crafting.smelterCraftTimeMultiplier[machineTierNum - 4]).toLong()
        return longArrayOf(multipliedRecipeCEt.energy, multipliedRecipeTime)
    }

    override fun prepareRecipe(recipe: Recipe): Boolean {
        val multipliedRecipeCEt = ClayEnergy(
            (recipe.cePerTick.energy.toDouble() * ConfigTierBalance.crafting.smelterConsumingEnergyMultiplier[metaTileEntity.tier.numeric - 4]).toLong()
        )
        val multipliedRecipeTime = (
            recipe.duration * ConfigTierBalance.crafting.smelterCraftTimeMultiplier[metaTileEntity.tier.numeric - 4]
        ).toLong()
        if (!this.drawEnergy(multipliedRecipeCEt, simulate = true)) return false
        val outputs = recipe.copyOutputs()
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
            this.outputsFull = true
            return false
        }
        if (!recipe.matches(true, inputInventory, getTier())) return false
        this.itemOutputs = outputs
        this.recipeCEt = multipliedRecipeCEt
        this.requiredProgress = multipliedRecipeTime
        this.currentProgress = 1
        return true
    }

    private fun prepareVanillaFurnaceRecipe(smeltingResult: ItemStack): Boolean {
        require(!smeltingResult.isEmpty)
        if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, listOf(smeltingResult), true)) {
            this.outputsFull = true
            return false
        }
        this.inputInventory.extractItem(0, 1, false)

        val (cet, duration) = applyOverclock(BASE_CE_CONSUMPTION, FURNACE_RECIPE_TIME, ocHandler.compensatedFactor)
        this.itemOutputs = listOf(smeltingResult)
        this.recipeCEt = ClayEnergy(cet)
        this.requiredProgress = duration
        this.currentProgress = 1
        return true
    }

    private companion object {
        private const val FURNACE_RECIPE_TIME = 200L //ticks
        private val BASE_CE_CONSUMPTION = ClayEnergy(4)
    }
}