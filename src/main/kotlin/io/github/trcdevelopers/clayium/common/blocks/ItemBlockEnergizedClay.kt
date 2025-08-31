package io.github.trcdevelopers.clayium.common.blocks

import com.cleanroommc.modularui.utils.ItemCapabilityProvider
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.IClayEnergyProvider
import io.github.trcdevelopers.clayium.api.unification.material.CPropertyKey
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.blocks.material.BlockEnergizedClay
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