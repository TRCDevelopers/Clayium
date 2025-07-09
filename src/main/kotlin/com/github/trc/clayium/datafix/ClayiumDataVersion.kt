package com.github.trc.clayium.datafix

enum class ClayiumDataVersion {
    V0,
    V1_MORE_FILTERS,
    /**
     * Ranged machines now save its range using 2 [net.minecraft.util.math.BlockPos]es instead of [codechicken.lib.vec.Cuboid6].
     */
    V2_RANGED_MACHINES_CHANGE_FORMAT,
    ;

    companion object {
        val currentVersion = entries[entries.size - 1]
    }
}