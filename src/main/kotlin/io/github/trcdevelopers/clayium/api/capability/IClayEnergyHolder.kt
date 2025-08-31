package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.api.ClayEnergy
import net.minecraftforge.items.IItemHandler

/**
 * A Clay Energy (CE) storage, similar to the [net.minecraftforge.energy.IEnergyStorage].
 */
interface IClayEnergyHolder {

    /**
     * CE is generated from Items that has [IClayEnergyProvider] capability.
     * This handler is an inventory for that.
     */
    val energizedClayItemHandler: IItemHandler

    fun getEnergyStored(): ClayEnergy

    /**
     * @return true if energy can/was drained, otherwise false
     */
    fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean

    /**
     * Low tier machines can generate energy from clicking GUI button or waterwheels.
     * This method is used in those cases.
     */
    fun addEnergy(ce: ClayEnergy)

    /**
     * Checks if the holder has enough energy to perform an operation.
     * **If not enough, you should try to consume an Energized Clay itemStack from the [energizedClayItemHandler].**
     */
    fun hasEnoughEnergy(ce: ClayEnergy): Boolean
}