package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.common.reflect.BlockReflect
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
 * Capability interface for items.
 * Right-clicking a machine with an item having this capability will apply the filter to the machine.
 */
interface IItemFilter : INBTSerializable<NBTTagCompound> {

    /**
     * Given stack must not be modified in this method.
     */
    fun test(stack: ItemStack): Boolean

    /**
     * default implementation gets a silktouch drop from the block, and [test]s it.
     */
    fun testBlock(world: World, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        val silkTouchDrop = BlockReflect.getSilkTouchDrop(state.block, state)

        return this.test(silkTouchDrop)
    }

    // For nullability. There is no nullability annotation in the original `INBTSerializable` interface.
    override fun serializeNBT(): NBTTagCompound
    override fun deserializeNBT(nbt: NBTTagCompound)

    companion object {
        val ALWAYS_FALSE: IItemFilter = object : IItemFilter {
            override fun test(stack: ItemStack): Boolean = false

            override fun serializeNBT(): NBTTagCompound = NBTTagCompound()

            override fun deserializeNBT(nbt: NBTTagCompound) {}
        }
    }
}