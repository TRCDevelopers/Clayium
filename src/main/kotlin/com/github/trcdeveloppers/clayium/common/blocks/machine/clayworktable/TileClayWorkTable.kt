package com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable

import com.github.trcdeveloppers.clayium.common.items.ClayiumItems.getItem
import com.github.trcdeveloppers.clayium.common.recipe.clayworktable.ClayWorkTableRecipe
import com.github.trcdeveloppers.clayium.common.recipe.clayworktable.ClayWorkTableRecipeManager
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

class TileClayWorkTable : TileEntity() {
    private val handler = ItemStackHandler(4)
    var craftingProgress = 0
    var requiredProgress = 0
    private var currentRecipe: ClayWorkTableRecipe? = null
    private val recipeManager = ClayWorkTableRecipeManager.INSTANCE
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("Inventory", handler.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        handler.deserializeNBT(compound.getCompoundTag("Inventory"))
        super.readFromNBT(compound)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ITEM_HANDLER_CAPABILITY) handler as T else super.getCapability(capability, facing)
    }

    private val currentTool: ItemStack
        get() = handler.getStackInSlot(2)

    @SideOnly(Side.CLIENT)
    fun getCraftingProgressScaled(scale: Int): Int {
        return if (requiredProgress == 0) 0 else craftingProgress * scale / requiredProgress
    }

    fun canPushButton(id: Int): Boolean {
        return canStartCraft(handler.getStackInSlot(0), ClayWorkTableMethod.fromId(id)
            ?: throw IllegalArgumentException("Invalid button id."))
    }

    fun pushButton(id: Int) {
        val input = handler.getStackInSlot(0)
        val method: ClayWorkTableMethod = ClayWorkTableMethod.fromId(id)
            ?: throw IllegalArgumentException("Invalid button id.")
        val recipe = recipeManager.getRecipeFor(input, method)
            ?: throw NullPointerException("Button pushed without any valid recipe.")
        if (currentRecipe !== recipe) {
            currentRecipe = recipe
            requiredProgress = recipe.clicks
            craftingProgress = 0
        }
        craftingProgress++
        if (craftingProgress >= requiredProgress) {
            input.count -= currentRecipe!!.input.count
            if (handler.getStackInSlot(2).isEmpty) {
                handler.setStackInSlot(2, currentRecipe!!.primaryOutput.copy())
            } else {
                handler.getStackInSlot(2).count += currentRecipe!!.primaryOutput.count
            }
            resetRecipe()
        }
    }

    private fun canStartCraft(input: ItemStack, method: ClayWorkTableMethod): Boolean {
        val recipe = recipeManager.getRecipeFor(input, method) ?: return false
        if (method == ClayWorkTableMethod.ROLLING_PIN && handler.getStackInSlot(1).item !== getItem("clay_rolling_pin")) {
            return false
        }
        if ((method == ClayWorkTableMethod.CUT_PLATE || method == ClayWorkTableMethod.CUT)
            && !(handler.getStackInSlot(1).item === getItem("clay_slicer")
                    || handler.getStackInSlot(1).item === getItem("clay_spatula"))
        ) {
            return false
        }
        if (method == ClayWorkTableMethod.CUT_DISC && handler.getStackInSlot(1).item !== getItem("clay_spatula")) {
            return false
        }

        return if (recipe.hasSecondaryOutput()) {
            recipe.canOutputPrimary(handler.getStackInSlot(2)) && recipe.canOutputSecondary(handler.getStackInSlot(3))
        } else {
            recipe.canOutputPrimary(handler.getStackInSlot(2))
        }
    }

    private fun resetRecipe() {
        currentRecipe = null
        requiredProgress = 0
        craftingProgress = 0
    }

    fun resetRecipeIfEmptyInput() {
        if (handler.getStackInSlot(0).isEmpty) {
            resetRecipe()
        }
    }

    companion object {
        private val ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}
