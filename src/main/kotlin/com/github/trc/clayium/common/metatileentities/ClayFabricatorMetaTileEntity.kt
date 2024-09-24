package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.AbstractWorkable
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.material.Clay
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.asWidgetResizing
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.blocks.ItemBlockMaterial
import com.github.trc.clayium.common.util.TransferUtils
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import kotlin.math.pow

class ClayFabricatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val maxClayCompressionLevel: Int,
    private val craftTimeLogic: (compressionLevel: Int, stackCount: Int) -> Long
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, validOutputModesLists[1], name = "clay_fabricator") {
    override val faceTexture = clayiumId("blocks/clay_fabricator")

    override val importItems = NotifiableItemStackHandler(this, 1, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    val autoIoHandler = AutoIoHandler.Combined(this)

    private val workable = ClayFabricatorRecipeLogic()

    override fun onPlacement() {
        this.setInput(EnumFacing.UP, MachineIoMode.ALL)
        this.setOutput(EnumFacing.DOWN, MachineIoMode.ALL)
        super.onPlacement()
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        syncManager.syncValue("clay_energy", SyncHandlers.longNumber({ workable.currentCe.energy }, { workable.currentCe = ClayEnergy(it) }))
        val slotsAndProgressBar = Row().widthRel(0.7f).height(26)
            .child(largeSlot(SyncHandlers.itemSlot(importItems, 0).singletonSlotGroup())
                .align(Alignment.CenterLeft))
            .child(workable.getProgressBar(syncManager).align(Alignment.Center))
            .child(largeSlot(SyncHandlers.itemSlot(exportItems, 0).accessibility(false, true))
                .align(Alignment.CenterRight))

        return super.buildMainParentWidget(syncManager)
            .child(slotsAndProgressBar.align(Alignment.Center))
            .child(IKey.dynamic { workable.currentCe.format() }.asWidgetResizing()
                .left(0).bottom(10))
    }

    override fun createMetaTileEntity() = ClayFabricatorMetaTileEntity(metaTileEntityId, tier, maxClayCompressionLevel, craftTimeLogic)

    private inner class ClayFabricatorRecipeLogic : AbstractWorkable(this@ClayFabricatorMetaTileEntity) {
        private var cePerTick = ClayEnergy.ZERO
        var currentCe = ClayEnergy.ZERO
        override fun trySearchNewRecipe() {
            val inputStack = importItems.getStackInSlot(0)
            if (inputStack.isEmpty) return invalidateInput()

            val inputItem = inputStack.item
            if (inputItem == Blocks.CLAY.getAsItem()) {
                prepareVanillaClay(inputStack.count)
                return
            }

            if (inputItem is ItemBlockMaterial) {
                val material = inputItem.blockMaterial.getCMaterial(inputStack)
                val clayProperty = material.getPropOrNull(CPropertyKey.CLAY)
                if (clayProperty == null || clayProperty.compressionLevel > maxClayCompressionLevel) return invalidateInput()
                prepareCMaterialClay(material, clayProperty, inputStack.count)
            }
        }

        private fun prepareVanillaClay(count: Int) {
            val outputs = listOf(ItemStack(Blocks.CLAY, count))
            if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
                this.outputsFull = true
                return
            }
            this.itemOutputs = outputs
            this.requiredProgress = 1
            this.currentProgress = 1
        }

        private fun prepareCMaterialClay(material: CMaterial, clayProperty: Clay, count: Int) {
            val outputs = listOf(OreDictUnifier.get(OrePrefix.block, material, count))
            if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, outputs, true)) {
                this.outputsFull = true
                return
            }
            this.itemOutputs = outputs
            this.requiredProgress = (craftTimeLogic(clayProperty.compressionLevel, count) / overclockHandler.compensatedFactor).toLong()
            this.currentProgress = 1
            if (clayProperty.energy != null) {
                this.cePerTick = ClayEnergy((clayProperty.energy * count).energy / this.requiredProgress)
            }
        }

        override fun updateWorkingProgress() {
            super.updateWorkingProgress()
            this.currentCe = this.cePerTick * this.currentProgress
        }

        override fun completeWork() {
            super.completeWork()
            this.cePerTick = ClayEnergy.ZERO
        }

        override fun showRecipesInJei() {}

        private fun invalidateInput() {
            this.invalidInputsForRecipes = true
        }

        override fun serializeNBT(): NBTTagCompound {
            val data = super.serializeNBT()
            data.setLong("currentCe", currentCe.energy)
            data.setLong("cePerTick", cePerTick.energy)
            return data
        }

        override fun deserializeNBT(data: NBTTagCompound) {
            super.deserializeNBT(data)
            currentCe = ClayEnergy(data.getLong("currentCe"))
            cePerTick = ClayEnergy(data.getLong("cePerTick"))
        }

        override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
            super.addProbeInfo(mode, probeInfo, player, world, state, hitData)
            if (isWorking) {
                probeInfo.text("Generating ${TextFormatting.GREEN}${cePerTick.formatWithoutUnit()}${TextFormatting.WHITE} CE/t")
            }
        }
    }

    companion object {
        /*
         * craft time functions
         * calculation logic is same as original. you can find that on the japanese wiki page: https://clayium.wiki.fc2.com/wiki/%E6%A9%9F%E6%A2%B0
         */
        private fun getCraftTime(compLevel: Int, stackCount: Int, maxCompLevel: Int, compMitigationFactor: Double, countMitigationFactor: Double, eff: Double): Long {
            return (64 * 10.0.pow(maxCompLevel) * (stackCount.toDouble() / 64.0).pow(countMitigationFactor) * compMitigationFactor.pow(compLevel - maxCompLevel) * 20.0 / eff).toLong()
        }

        fun mk1(compLevel: Int, stackCount: Int): Long {
            return getCraftTime(compLevel, stackCount, maxCompLevel = 11, compMitigationFactor = 5.0, countMitigationFactor = 0.85, eff = 4.5*10.0.pow(7))
        }

        fun mk2(compLevel: Int, stackCount: Int): Long {
            return getCraftTime(compLevel, stackCount, maxCompLevel = 13, compMitigationFactor = 2.0, countMitigationFactor = 0.3, eff = 10.0.pow(9))
        }

        fun mk3(compLevel: Int, stackCount: Int): Long {
            return getCraftTime(compLevel, stackCount, maxCompLevel = 13, compMitigationFactor = 1.3, countMitigationFactor = 0.06, eff = 10.0.pow(12))
        }
    }
}