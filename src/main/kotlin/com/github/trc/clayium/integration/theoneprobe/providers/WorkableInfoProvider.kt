package com.github.trc.clayium.integration.theoneprobe.providers

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.capability.AbstractWorkable
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import kotlin.math.round

private const val COLOR_ENABLED_ARGB: Int = 0xFF4CBB17.toInt()
private const val COLOR_DISABLED_ARGB: Int = 0xFFBB1C28.toInt()
private const val BORDER_COLOR: Int = 0xFF555555.toInt()
private const val HALF_HOUR_TICKS: Int = 30 * 60 * 20
private const val ONE_MIN_TICKS: Double = 60 * 20.0

class WorkableInfoProvider : CapabilityInfoProvider<AbstractWorkable>() {
    override val capability: Capability<AbstractWorkable> = ClayiumTileCapabilities.WORKABLE

    override fun addProbeInfo(capability: AbstractWorkable, mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
        if (!capability.isWorking) return

        var progress = capability.currentProgress
        var maxProgress = capability.requiredProgress

        val suffix = if (maxProgress > HALF_HOUR_TICKS) {
            progress = round(progress / ONE_MIN_TICKS).toLong()
            maxProgress = round(maxProgress / ONE_MIN_TICKS).toLong()
            " / $maxProgress min"
        } else if (maxProgress > 20) {
            progress = round(progress / 20.0).toLong()
            maxProgress = round(maxProgress / 20.0).toLong()
            " / $maxProgress s"
        } else {
            " / $maxProgress t"
        }

        val color = if (capability.isWorkingEnabled) COLOR_ENABLED_ARGB else COLOR_DISABLED_ARGB
        if (capability.requiredProgress > 0) {
            probeInfo.progress(
                progress, maxProgress, probeInfo.defaultProgressStyle()
                    .suffix(suffix)
                    .filledColor(color)
                    .alternateFilledColor(color)
                    .borderColor(BORDER_COLOR)
                    .numberFormat(NumberFormat.COMMAS)
            )
        }
    }

    override fun getID() = "$MOD_ID:workable_info_provider"
}