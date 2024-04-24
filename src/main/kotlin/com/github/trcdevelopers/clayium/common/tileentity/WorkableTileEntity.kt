package com.github.trcdevelopers.clayium.common.tileentity

import com.github.trcdevelopers.clayium.common.items.handler.NotifiableItemStackHandler
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.tileentity.trait.BasicRecipeLogic
import com.github.trcdevelopers.clayium.common.tileentity.trait.ClayEnergyHolder
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper

abstract class WorkableTileEntity : TileEntityMachine() {

    protected lateinit var recipeRegistry: RecipeRegistry<*>

    protected abstract val inputSize: Int
    protected abstract val outputSize: Int

    protected lateinit var ceSlot: ClayEnergyHolder
    protected lateinit var workable: BasicRecipeLogic

    override lateinit var inputInventory: IItemHandlerModifiable
    override lateinit var outputInventory: IItemHandlerModifiable
    override lateinit var combinedInventory: IItemHandler

    override fun initializeByTier(tier: Int) {
        super.initializeByTier(tier)
        inputInventory = NotifiableItemStackHandler(this, inputSize, this, false)
        outputInventory = NotifiableItemStackHandler(this, outputSize, this, true)
        combinedInventory = CombinedInvWrapper(inputInventory, outputInventory)
    }

    override fun writeToNBT(data: NBTTagCompound): NBTTagCompound {
        data.setString("recipeRegistry", recipeRegistry.category.categoryName)
        ceSlot.writeToNBT(data)
        workable.writeToNBT(data)
        return super.writeToNBT(data)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        recipeRegistry = CRecipes.findRegistry(data.getString("recipeRegistry")) ?: throw IllegalArgumentException("RecipeRegistry does not exist")
        ceSlot = ClayEnergyHolder(this, tier).apply { readFromNBT(data) }
        workable = BasicRecipeLogic(this, tier, ceSlot, recipeRegistry).apply { readFromNBT(data) }
    }

    override fun update() {
        super.update()
        workable.update()
    }
}