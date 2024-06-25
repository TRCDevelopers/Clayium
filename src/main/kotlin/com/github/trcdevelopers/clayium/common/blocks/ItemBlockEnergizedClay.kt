package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import com.github.trcdevelopers.clayium.api.capability.impl.SimpleClayEnergyProvider
import com.github.trcdevelopers.clayium.common.unification.material.PropertyKey
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

class ItemBlockEnergizedClay(block: BlockEnergizedClay) : ItemBlockMaterial(block) {
    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object : ICapabilityProvider {
            override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) = capability == ClayiumCapabilities.ENERGIZED_CLAY

            override fun <T : Any> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
                if (capability == ClayiumCapabilities.ENERGIZED_CLAY) {
                    return ClayiumCapabilities.ENERGIZED_CLAY.cast(
                        SimpleClayEnergyProvider(blockMaterial.getCMaterial(stack).getProperty(PropertyKey.ENERGIZED_CLAY).energy)
                    )
                }
                return null
            }

        }
    }
}