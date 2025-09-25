package io.github.trcdevelopers.clayium.api.capability

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilitySerializable

/**
 * This is a replacement of `IExtendedEntityProperties`.
 * This is not intended to be inherited by any class.
 */
class ClayiumPlayerData : ICapabilitySerializable<NBTTagCompound> {
    /**
     * Flight Gadget
     */
    var wasFlying = false

    /**
     * Clay Guns
     */
    var clayGunCooldown = 0

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CAPABILITY
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CAPABILITY) {
            CAPABILITY.cast(this)
        } else {
            null
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()
        nbt.setBoolean("wasFlying", wasFlying)
        nbt.setInteger("clayGunCooldown", clayGunCooldown)
        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.wasFlying = nbt.getBoolean("wasFlying")
        this.clayGunCooldown = nbt.getInteger("clayGunCooldown")
    }

    companion object {
        @CapabilityInject(ClayiumPlayerData::class)
        lateinit var CAPABILITY: Capability<ClayiumPlayerData>
    }
}