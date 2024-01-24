package com.github.trcdevelopers.clayium.common.interfaces

interface IClayEnergyContainer {
    var clayEnergy: Long
    fun addClayEnergy(energy: Long): Long
}
