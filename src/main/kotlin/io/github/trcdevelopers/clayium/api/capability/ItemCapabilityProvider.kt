package io.github.trcdevelopers.clayium.api.capability

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

interface ItemCapabilityProvider : ICapabilityProvider {

    fun <T> getCapability(capability: Capability<T>): T?

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return getCapability(capability)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return getCapability(capability) != null
    }
}