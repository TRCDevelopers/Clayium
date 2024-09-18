package com.github.trc.clayium.common.items

import com.github.trc.clayium.common.util.ToolClasses
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

class ItemClaySteelTool : ItemPickaxe(ToolMaterial.DIAMOND) {

    override fun onItemUse(player: EntityPlayer, world: World, targetPos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (!player.isSneaking) return EnumActionResult.PASS
        val oldState = world.getBlockState(targetPos)
        val block = oldState.block
        val pos = if (block.isReplaceable(world, targetPos)) targetPos else targetPos.offset(facing)
        val clayStack = ItemStack(Blocks.CLAY)
        val clayBlock = Blocks.CLAY
        val clayState = clayBlock.defaultState
        if (!(player.canPlayerEdit(pos, facing, clayStack) && world.mayPlace(Blocks.CLAY, pos, false, facing, player))) return EnumActionResult.FAIL

        if (world.setBlockState(pos, clayState, Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
            val soundType = clayBlock.getSoundType(clayState, world, pos, player)
            world.playSound(player, pos, soundType.placeSound, SoundCategory.BLOCKS, (soundType.volume + 1f) / 2f, soundType.pitch * 0.8f)
        }
        return EnumActionResult.SUCCESS
    }

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