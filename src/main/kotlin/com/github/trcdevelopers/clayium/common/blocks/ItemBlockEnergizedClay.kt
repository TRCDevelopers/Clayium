package com.github.trcdevelopers.clayium.common.blocks

import com.cleanroommc.modularui.utils.ItemCapabilityProvider
import com.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import com.github.trcdevelopers.clayium.api.capability.impl.SimpleClayEnergyProvider
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

class ItemBlockEnergizedClay(block: BlockEnergizedClay, orePrefix: OrePrefix, ) : ItemBlockMaterial(block, orePrefix) {
    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object : ItemCapabilityProvider {
            override fun <T : Any> getCapability(capability: Capability<T?>): T? {
                if (capability == ClayiumCapabilities.ENERGIZED_CLAY) {
                    val energy = blockMaterial.getCMaterial(stack).getPropOrNull(PropertyKey.CLAY)?.energy
                    if (energy != null) {
                        return ClayiumCapabilities.ENERGIZED_CLAY.cast( SimpleClayEnergyProvider(energy) )
                    }
                }
                return null
            }
        }
    }
}