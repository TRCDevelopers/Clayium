package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.util.next
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
        val mode = getNextMode(stack)
        if (mode == null) {
            stack.tagCompound = NBTTagCompound().apply { setInteger("mode", Mode.SINGLE.ordinal) }
        } else {
            stack.tagCompound!!.setInteger("mode", mode.ordinal)
            playerIn.sendMessage(TextComponentString("Set mode to ${mode.name.lowercase()}"))
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
        poses.forEach { worldIn.destroyBlock(it, true) }
        return true
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        return super.getDestroySpeed(stack, state) * SPEED_MULTIPLIER
    }

    private fun getNextMode(stack: ItemStack): Mode? {
        if (!stack.hasTagCompound()) return null
        val i = stack.tagCompound!!.getInteger("mode")
        if (i < 0 || i >= Mode.entries.size) return Mode.SINGLE

        return Mode.entries[i].next()
    }

    enum class Mode {
        SINGLE,
        RANGED,
        CUSTOM,
    }
}