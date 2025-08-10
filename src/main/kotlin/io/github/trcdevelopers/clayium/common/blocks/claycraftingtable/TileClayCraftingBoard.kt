package io.github.trcdevelopers.clayium.common.blocks.claycraftingtable

import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.common.inventory.ItemHandlerWrappedInventoryCrafting
import io.github.trcdevelopers.clayium.common.util.DummyContainer
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class TileClayCraftingBoard : TileEntity(), IMarkDirty {

    val inventory = object : ClayiumItemStackHandler(this, 10) {
        override fun onContentsChanged(slot: Int) {
            super.onContentsChanged(slot)
            if (world.isRemote) return
            this@TileClayCraftingBoard.onCraftMatrixChanged()
        }
    }

    private val craftMatrix = ItemHandlerWrappedInventoryCrafting(inventory, DummyContainer)
    var currentRecipe: IRecipe? = null
        private set

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val data = super.writeToNBT(compound)
        CUtils.writeItems(inventory, "inventory", data)
        return data
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        CUtils.readItems(inventory, "inventory", compound)
    }

    override fun markAsDirty() = this.markDirty()

    private fun onCraftMatrixChanged() {
        this.currentRecipe = CraftingManager.findMatchingRecipe(this.craftMatrix, this.world)
    }
}