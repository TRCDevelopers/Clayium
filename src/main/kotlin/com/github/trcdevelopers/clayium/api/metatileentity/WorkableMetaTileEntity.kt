package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class WorkableMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: Int,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    faceTexture: ResourceLocation?,
    val recipeRegistry: RecipeRegistry<*>,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, faceTexture) {

//    constructor(
//        metaTileEntityId: ResourceLocation, tier: Int,
//        validInputModes: List<MachineIoMode>, validOutputModes: List<MachineIoMode>,
//        machineName: ResourceLocation,
//        inputSize: Int, outputSize: Int
//    ) : this(metaTileEntityId, tier, validInputModes, validOutputModes, "machine.${CValues.MOD_ID}.${machineName.path}", machineName, inputSize, outputSize)

    val inputSize = recipeRegistry.maxInputs
    val outputSize = recipeRegistry.maxOutputs

    override val importItems = NotifiableItemStackHandler(this, inputSize, this, false)
    override val exportItems = NotifiableItemStackHandler(this, outputSize, this, true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    override val autoIoHandler = AutoIoHandler.Combined(this)

    val clayEnergyHolder = ClayEnergyHolder(this)
    abstract val workable: AbstractRecipeLogic

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation("${metaTileEntityId.namespace}:${faceTexture}", "tier=$tier"))
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("$metaTileEntityId")
    }
}