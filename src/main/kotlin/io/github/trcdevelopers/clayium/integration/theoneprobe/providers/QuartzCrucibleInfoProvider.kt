package io.github.trcdevelopers.clayium.integration.theoneprobe.providers

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.common.blocks.BlockQuartzCrucible
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class QuartzCrucibleInfoProvider : IProbeInfoProvider {
    override fun getID(): String {
        return "$MOD_ID:quartz_crucible_info_provider"
    }

    override fun addProbeInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
        val tileEntity = world.getTileEntity(data.pos) as? BlockQuartzCrucible.QuartzCrucibleTileEntity ?: return
        val requiredTicks = tileEntity.ingotQuantity * BlockQuartzCrucible.TICKS_PER_ITEM
        val currentTicks = tileEntity.ticked

        info.progress(
            currentTicks, requiredTicks, info.defaultProgressStyle()
                .suffix(" / $requiredTicks ticks")
                .numberFormat(NumberFormat.COMMAS)
        )
    }
}