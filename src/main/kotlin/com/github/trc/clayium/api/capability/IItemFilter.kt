package com.github.trc.clayium.api.capability

import com.github.trc.clayium.common.reflect.BlockReflect
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
 * It is similar to the Predicate<ItemStack>, but it is impossible to serialize the nested lambda.
 */
interface IItemFilter : INBTSerializable<NBTTagCompound> {

    /**
     * Do not modify the stack.
     */
    fun test(stack: ItemStack): Boolean

    /**
     * default implementation gets a silktouch drop from the block, and [test] it.
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