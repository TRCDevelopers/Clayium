package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.util.ResourceLocation

class ClayReactorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
): MultiblockControllerBase(
    metaTileEntityId, tier,
    listOf(MachineIoMode.NONE, MachineIoMode.ALL, MachineIoMode.CE),
    listOf(MachineIoMode.NONE, MachineIoMode.ALL),
    "machine.${CValues.MOD_ID}.clay_blast_furnace",
    CRecipes.CLAY_REACTOR
) {
    override fun isConstructed(): Boolean {
        TODO("Not yet implemented")
    }

    override val workable: MultiblockRecipeLogic
        get() = TODO("Not yet implemented")

    override fun createMetaTileEntity(): MetaTileEntity {
        TODO("Not yet implemented")
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        TODO("Not yet implemented")
    }
}