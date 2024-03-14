package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.common.blocks.BlockMachineTemp.Companion.IS_PIPE
import net.minecraft.block.properties.PropertyBool

/**
 * Root of all Clayium BlockMachines.
 *
 * - can be piped (see [IS_PIPE])
 */
class BlockMachineTemp {
    companion object {
        val IS_PIPE = PropertyBool.create("is_pipe")
    }
}