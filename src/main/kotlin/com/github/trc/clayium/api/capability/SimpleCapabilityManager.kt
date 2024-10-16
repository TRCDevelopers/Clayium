package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.pan.IPanAdapter
import com.github.trc.clayium.api.pan.IPanCable
import com.github.trc.clayium.api.pan.IPanUser
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager

object SimpleCapabilityManager {
    private fun <T> registerCapabilityWithNoDefault(capabilityClass: Class<T>) {
        CapabilityManager.INSTANCE.register(capabilityClass, object : Capability.IStorage<T> {
            override fun writeNBT(capability: Capability<T?>?, instance: T?, side: EnumFacing?): NBTBase? {
                throw UnsupportedOperationException("Capability $capabilityClass does not support default instances")
            }
            override fun readNBT(capability: Capability<T?>?, instance: T?, side: EnumFacing?, nbt: NBTBase?) {
                throw UnsupportedOperationException("Capability $capabilityClass does not support default instances")
            }
        }
        ) { throw UnsupportedOperationException("Capability $capabilityClass does not support default instances") }
    }

    fun registerCapabilities() {
        registerCapabilityWithNoDefault(IClayEnergyHolder::class.java)
        registerCapabilityWithNoDefault(AutoIoHandler::class.java)
        registerCapabilityWithNoDefault(IClayLaserSource::class.java)
        registerCapabilityWithNoDefault(IClayLaserAcceptor::class.java)
        registerCapabilityWithNoDefault(IControllable::class.java)
        registerCapabilityWithNoDefault(AbstractWorkable::class.java)
        registerCapabilityWithNoDefault(AbstractRecipeLogic::class.java)
        registerCapabilityWithNoDefault(IPipeConnectable::class.java)

        registerCapabilityWithNoDefault(ISynchronizedInterface::class.java)
        registerCapabilityWithNoDefault(IItemFilter::class.java)
        registerCapabilityWithNoDefault(IClayEnergyProvider::class.java)
        registerCapabilityWithNoDefault(IConfigurationTool::class.java)
        registerCapabilityWithNoDefault(IPanCable::class.java)
        registerCapabilityWithNoDefault(IPanUser::class.java)
        registerCapabilityWithNoDefault(IPanAdapter::class.java)
    }
}