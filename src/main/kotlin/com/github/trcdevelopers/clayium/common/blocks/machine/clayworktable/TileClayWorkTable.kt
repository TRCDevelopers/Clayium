package com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable

import com.github.trcdevelopers.clayium.common.items.ClayiumItems.getItem
import com.github.trcdevelopers.clayium.common.recipe.CRecipes
import com.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class TileClayWorkTable : TileEntity() {

    private val itemHandler = ItemStackHandler(4)
    var craftingProgress = 0
    var requiredProgress = 0
    private var currentRecipe: ClayWorkTableRecipe? = null

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("Inventory", itemHandler.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        itemHandler.deserializeNBT(compound.getCompoundTag("Inventory"))
        super.readFromNBT(compound)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ITEM_HANDLER_CAPABILITY) itemHandler as T else super.getCapability(capability, facing)
    }

    private val currentTool: ItemStack
        get() = itemHandler.getStackInSlot(2)

    @SideOnly(Side.CLIENT)
    fun getCraftingProgressScaled(scale: Int): Int {
        return if (requiredProgress == 0) 0 else craftingProgress * scale / requiredProgress
    }

    fun canPushButton(id: Int): Boolean {
        return canStartCraft(itemHandler.getStackInSlot(0), ClayWorkTableMethod.fromId(id)
            ?: throw IllegalArgumentException("Invalid button id."))
    }

    fun pushButton(id: Int) {
        val input = itemHandler.getStackInSlot(0)
        val method: ClayWorkTableMethod = ClayWorkTableMethod.fromId(id)
            ?: throw IllegalArgumentException("Invalid button id.")
        val recipe = CRecipes.getClayWorkTableRecipe(input, method)
            ?: throw NullPointerException("Button pushed without any valid recipe! This should not happen.")
        if (currentRecipe !== recipe) {
            currentRecipe = recipe
            requiredProgress = recipe.clicks
            craftingProgress = 0
        }
        craftingProgress++
        if (craftingProgress >= requiredProgress) {
            input.count -= recipe.input.amount
            if (itemHandler.getStackInSlot(2).isEmpty) {
                itemHandler.setStackInSlot(2, recipe.primaryOutput)
            } else {
                itemHandler.getStackInSlot(2).count += recipe.primaryOutput.count
            }
            resetRecipe()
        }
    }

    private fun canStartCraft(input: ItemStack, method: ClayWorkTableMethod): Boolean {
        val recipe = CRecipes.getClayWorkTableRecipe(input, method) ?: return false
        if (method == ClayWorkTableMethod.ROLLING_PIN && itemHandler.getStackInSlot(1).item !== getItem("clay_rolling_pin")) {
            return false
        }
        if ((method == ClayWorkTableMethod.CUT_PLATE || method == ClayWorkTableMethod.CUT)
            && !(itemHandler.getStackInSlot(1).item === getItem("clay_slicer")
                    || itemHandler.getStackInSlot(1).item === getItem("clay_spatula"))
        ) {
            return false
        }
        if (method == ClayWorkTableMethod.CUT_DISC && itemHandler.getStackInSlot(1).item !== getItem("clay_spatula")) {
            return false
        }

        val outputSlot = itemHandler.getStackInSlot(2)
        val secondaryOutputSlot = itemHandler.getStackInSlot(3)

        val canOutputPrimary = (outputSlot.isEmpty || (outputSlot.isItemEqual(recipe.primaryOutput) && outputSlot.count + recipe.primaryOutput.count <= outputSlot.maxStackSize))
        val canOutputSecondary = (secondaryOutputSlot.isEmpty || (secondaryOutputSlot.isItemEqual(recipe.secondaryOutput) && secondaryOutputSlot.count + recipe.secondaryOutput.count <= secondaryOutputSlot.maxStackSize))

        return canOutputPrimary && (!recipe.hasSecondaryOutput() || canOutputSecondary)
    }

    private fun resetRecipe() {
        currentRecipe = null
        requiredProgress = 0
        craftingProgress = 0
    }

    fun resetRecipeIfEmptyInput() {
        if (itemHandler.getStackInSlot(0).isEmpty) {
            resetRecipe()
        }
    }

    companion object {
        private val ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}
