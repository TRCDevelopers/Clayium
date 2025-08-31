package io.github.trcdevelopers.clayium.api.extensions

import net.minecraft.util.math.BlockPos

operator fun BlockPos.component1(): Int = x
operator fun BlockPos.component2(): Int = y
operator fun BlockPos.component3(): Int = z
