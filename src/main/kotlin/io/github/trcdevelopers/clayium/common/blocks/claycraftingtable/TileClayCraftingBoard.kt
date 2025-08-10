package io.github.trcdevelopers.clayium.common.blocks.claycraftingtable

import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.CLAY_CRAFTING_BOARD_RECIPE
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.SyncedTileEntityBase
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.common.inventory.ItemHandlerWrappedInventoryCrafting
import io.github.trcdevelopers.clayium.common.util.DummyContainer
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer

class TileClayCraftingBoard : SyncedTileEntityBase(), IMarkDirty {

    val inventory = object : ClayiumItemStackHandler(this, 10) {
        override fun onContentsChanged(slot: Int) {
            super.onContentsChanged(slot)
            if (world.isRemote) return
            this@TileClayCraftingBoard.onCraftMatrixChanged()
        }
    }

    private val craftMatrix = ItemHandlerWrappedInventoryCrafting(inventory, DummyContainer)
    var currentRecipe: IRecipe? = null
        private set(value) {
            val syncFlag = !this.world.isRemote && (field?.registryName != value?.registryName)
            field = value
            if (syncFlag) {
                this.writeCustomData(CLAY_CRAFTING_BOARD_RECIPE) {
                    if (value == null) {
                        writeBoolean(false)
                    } else {
                        writeBoolean(true)
                        writeResourceLocation(value.registryName!!)
                    }
                }
            }
        }

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

    override fun writeInitialSyncData(buf: PacketBuffer) {}
    override fun receiveInitialSyncData(buf: PacketBuffer) {}

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == CLAY_CRAFTING_BOARD_RECIPE) {
            if (buf.readBoolean()) {
                this.currentRecipe = CraftingManager.getRecipe(buf.readResourceLocation())
            } else {
                this.currentRecipe = null
            }
        }
    }
}