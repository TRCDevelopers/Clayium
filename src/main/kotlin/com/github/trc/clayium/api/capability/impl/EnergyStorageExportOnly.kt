package com.github.trc.clayium.api.capability.impl

import net.minecraftforge.energy.IEnergyStorage

class EnergyStorageExportOnly(
    delegate: IEnergyStorage,
) : IEnergyStorage by delegate {
    override fun canReceive(): Boolean {
        return false
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        return 0
    }
}