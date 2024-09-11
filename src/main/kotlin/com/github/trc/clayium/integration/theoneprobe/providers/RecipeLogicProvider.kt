package com.github.trc.clayium.integration.theoneprobe.providers

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

class RecipeLogicProvider : CapabilityInfoProvider<AbstractRecipeLogic>() {
    override val capability: Capability<AbstractRecipeLogic> = ClayiumTileCapabilities.RECIPE_LOGIC

    override fun addProbeInfo(capability: AbstractRecipeLogic, mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        if (!capability.isWorkingEnabled || !capability.isWorking) return
        val cet = capability.recipeCEt
        probeInfo.text("Using ${TextFormatting.RED}${cet.formatWithoutUnit()}${TextFormatting.WHITE} CE/t")
    }

    override fun getID() = "$MOD_ID:recipe_logic_provider"
}