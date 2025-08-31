package io.github.trcdevelopers.clayium.integration.theoneprobe.providers

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.IClayEnergyHolder
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

class ClayEnergyInfoProvider : CapabilityInfoProvider<IClayEnergyHolder>() {
    override val capability: Capability<IClayEnergyHolder> = ClayiumTileCapabilities.CLAY_ENERGY_HOLDER

    override fun addProbeInfo(capability: IClayEnergyHolder, mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        probeInfo.text(capability.getEnergyStored().format())
    }

    override fun getID() = "$MOD_ID:ce_container_provider"
}