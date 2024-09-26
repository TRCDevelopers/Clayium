package com.github.trc.clayium.common.blocks

import com.cleanroommc.modularui.utils.ItemCapabilityProvider
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IClayEnergyProvider
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.blocks.material.BlockEnergizedClay
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

class ItemBlockEnergizedClay(block: BlockEnergizedClay, orePrefix: OrePrefix, ) : ItemBlockMaterial(block, orePrefix) {
    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object : ItemCapabilityProvider {
            override fun <T : Any> getCapability(capability: Capability<T?>): T? {
                if (capability == ClayiumCapabilities.ENERGIZED_CLAY) {
                    val energy = blockMaterial.getCMaterial(stack).getPropOrNull(CPropertyKey.CLAY)?.energy
                    if (energy != null) {
                        return ClayiumCapabilities.ENERGIZED_CLAY.cast(IClayEnergyProvider { energy })
                    }
                }
                return null
            }
        }
    }
}