package com.github.trc.clayium.integration.theoneprobe.providers

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.capability.AbstractWorkable
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

class WorkableInfoProvider : CapabilityInfoProvider<AbstractWorkable>() {
    override val capability: Capability<AbstractWorkable> = ClayiumTileCapabilities.WORKABLE

    override fun addProbeInfo(capability: AbstractWorkable, mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        capability.addProbeInfo(mode, probeInfo, player, world, state, hitData)
    }

    override fun getID() = "$MOD_ID:workable_info_provider"
}