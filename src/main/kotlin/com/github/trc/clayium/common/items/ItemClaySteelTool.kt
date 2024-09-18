package com.github.trc.clayium.common.items

import com.github.trc.clayium.common.util.ToolClasses
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ItemClaySteelTool : ItemPickaxe(ToolMaterial.DIAMOND) {

    override fun onBlockDestroyed(stack: ItemStack, worldIn: World, state: IBlockState, pos: BlockPos, entityLiving: EntityLivingBase): Boolean {
        if (worldIn.isRemote) return true
        val facing = EnumFacing.getDirectionFromEntityLiving(pos, entityLiving)
        val poses = if (facing.axis.isHorizontal) {
            val pos1 = pos.offset(facing.rotateY()).offset(EnumFacing.DOWN)
            val pos2 = pos.offset(facing.rotateYCCW()).offset(EnumFacing.UP)
            BlockPos.getAllInBox(pos1, pos2)
        } else {
            val pos1 = pos.offset(facing.rotateAround(EnumFacing.Axis.X)).offset(facing.rotateAround(EnumFacing.Axis.Z))
            val pos2 = pos.offset(facing.rotateAround(EnumFacing.Axis.X), -1).offset(facing.rotateAround(EnumFacing.Axis.Z), -1)
            BlockPos.getAllInBox(pos1, pos2)
        }
        poses.forEach {
            val block = worldIn.getBlockState(it).block
            if (block.isToolEffective(ToolClasses.PICKAXE, worldIn.getBlockState(it))) {
                worldIn.destroyBlock(it, true)
            }
        }
        return true
    }
}