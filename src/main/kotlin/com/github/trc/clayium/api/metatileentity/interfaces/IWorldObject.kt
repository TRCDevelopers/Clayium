package com.github.trc.clayium.api.metatileentity.interfaces

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IWorldObject : IMarkDirty {
    val worldObj: World?
    val position: BlockPos?
}
