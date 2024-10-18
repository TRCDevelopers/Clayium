package com.github.trc.clayium.integration.theoneprobe.providers

import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

abstract class CapabilityInfoProvider<T> : IProbeInfoProvider {
    abstract val capability: Capability<T>

    protected abstract fun addProbeInfo(
        capability: T,
        mode: ProbeMode,
        probeInfo: IProbeInfo,
        player: EntityPlayer,
        world: World,
        state: IBlockState,
        hitData: IProbeHitData
    )

    override fun addProbeInfo(
        mode: ProbeMode,
        probeInfo: IProbeInfo,
        player: EntityPlayer,
        world: World,
        state: IBlockState,
        hitData: IProbeHitData
    ) {
        world.getTileEntity(hitData.pos)?.getCapability(capability, null)?.let { capability ->
            addProbeInfo(capability, mode, probeInfo, player, world, state, hitData)
        }
    }
}
