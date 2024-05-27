package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import com.github.trcdevelopers.clayium.common.recipe.Recipe
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes

class RecipeLogicClayFurnace(
    metaTileEntity: MetaTileEntity,
    unused: RecipeRegistry<*>,
    clayEnergyHolder: ClayEnergyHolder,
) : RecipeLogicEnergy(metaTileEntity, CRecipes.SMELTER, clayEnergyHolder) {

    override fun trySearchNewRecipe() {
        val smeltingResult = FurnaceRecipes.instance().getSmeltingResult(inputInventory.getStackInSlot(0)).copy()
        if (smeltingResult.isEmpty) return super.trySearchNewRecipe()
        prepareVanillaFurnaceRecipe(smeltingResult)
    }

    override fun prepareRecipe(recipe: Recipe) {
        val multipliedRecipeCEt = ClayEnergy(
            (recipe.cePerTick.energy.toDouble() * ConfigTierBalance.crafting.smelterConsumingEnergyMultiplier[metaTileEntity.tier.numeric - 4]).toLong()
        )
        val multipliedRecipeTime = (
            recipe.duration * ConfigTierBalance.crafting.smelterCraftTimeMultiplier[metaTileEntity.tier.numeric - 4]
        ).toInt()
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
        val machineTierNum = metaTileEntity.tier.numeric
        val multipliedRecipeCEt = ClayEnergy((BASE_CE_CONSUMPTION.energy.toDouble() * ConfigTierBalance.crafting.smelterConsumingEnergyMultiplier[machineTierNum - 4]).toLong())
        val multipliedRecipeTime = (FURNACE_RECIPE_TIME * ConfigTierBalance.crafting.smelterCraftTimeMultiplier[machineTierNum - 4]).toInt()

        this.itemOutputs = listOf(smeltingResult)
        this.recipeCEt = multipliedRecipeCEt
        this.requiredProgress = multipliedRecipeTime
        this.currentProgress = 1
    }

    private companion object {
        private const val FURNACE_RECIPE_TIME = 200 //ticks
        private val BASE_CE_CONSUMPTION = ClayEnergy(4)
    }
}