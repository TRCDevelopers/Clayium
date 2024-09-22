package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.HARDNESS_UNBREAKABLE
import com.github.trc.clayium.api.util.next
import com.github.trc.clayium.common.items.ItemClaySteelTool.Mode.*
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

private const val SPEED_MULTIPLIER = 6

class ItemClaySteelTool : ItemPickaxe(ToolMaterial.DIAMOND) {

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack?> {
        if (playerIn.isSneaking) return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn))
        if (worldIn.isRemote) return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))

        val stack = playerIn.getHeldItem(handIn)
        val mode = getMode(stack)
        if (mode == null) {
            stack.tagCompound = NBTTagCompound().apply { setInteger("mode", Mode.SINGLE.ordinal) }
        } else {
            val next = mode.next()
            stack.tagCompound!!.setInteger("mode", next.ordinal)
            playerIn.sendMessage(TextComponentString("Set mode to ${next.name.lowercase()}"))
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
    }

    override fun onItemUse(player: EntityPlayer, world: World, targetPos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (player.isSneaking) {
            val oldState = world.getBlockState(targetPos)
            val block = oldState.block
            val pos = if (block.isReplaceable(world, targetPos)) targetPos else targetPos.offset(facing)
            val clayStack = ItemStack(Blocks.CLAY)
            val clayBlock = Blocks.CLAY
            val clayState = clayBlock.defaultState
            if (!(player.canPlayerEdit(pos, facing, clayStack) && world.mayPlace(Blocks.CLAY, pos, false, facing, player))){
                return EnumActionResult.FAIL
            }

            if (world.setBlockState(pos, clayState, Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
                val soundType = clayBlock.getSoundType(clayState, world, pos, player)
                world.playSound(player, pos, soundType.placeSound, SoundCategory.BLOCKS, (soundType.volume + 1f) / 2f, soundType.pitch * 0.8f)
            }
            return EnumActionResult.SUCCESS
        }
        return EnumActionResult.PASS
    }

    override fun onBlockDestroyed(stack: ItemStack, worldIn: World, state: IBlockState, pos: BlockPos, entityLiving: EntityLivingBase): Boolean {
        if (worldIn.isRemote) return true
        when (getMode(stack)) {
            null, SINGLE -> super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving)
            RANGED -> {
                for (pos in getPoses(entityLiving, pos, 1)) {
                    if (worldIn.getBlockState(pos).getBlockHardness(worldIn, pos) == HARDNESS_UNBREAKABLE) continue
                    worldIn.destroyBlock(pos, true)
                    stack.damageItem(1, entityLiving)
                }
            }
            CUSTOM -> {
                for (pos in getPoses(entityLiving, pos, 2)) {
                    if (worldIn.getBlockState(pos).getBlockHardness(worldIn, pos) == HARDNESS_UNBREAKABLE) continue
                    worldIn.destroyBlock(pos, true)
                    stack.damageItem(1, entityLiving)
                }
            }
        }
        return true
    }

    private fun getPoses(player: EntityLivingBase, pos: BlockPos, range: Int): Iterable<BlockPos> {
        val facing = EnumFacing.getDirectionFromEntityLiving(pos, player)
        return if (facing.axis.isHorizontal) {
            val pos1 = pos.offset(facing.rotateY(), range).offset(EnumFacing.DOWN, range)
            val pos2 = pos.offset(facing.rotateYCCW(), range).offset(EnumFacing.UP, range)
            BlockPos.getAllInBox(pos1, pos2)
        } else {
            val pos1 = pos.offset(facing.rotateAround(EnumFacing.Axis.X), range).offset(facing.rotateAround(EnumFacing.Axis.Z), range)
            val pos2 = pos.offset(facing.rotateAround(EnumFacing.Axis.X), -range).offset(facing.rotateAround(EnumFacing.Axis.Z), -range)
            BlockPos.getAllInBox(pos1, pos2)
        }
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        return super.getDestroySpeed(stack, state) * SPEED_MULTIPLIER
    }

    private fun getMode(stack: ItemStack): Mode? {
        if (!stack.hasTagCompound()) return null
        val i = stack.tagCompound!!.getInteger("mode")
        if (i < 0 || i >= Mode.entries.size) return Mode.SINGLE

        return Mode.entries[i]
    }

    enum class Mode {
        SINGLE,
        RANGED,
        CUSTOM,
    }
}