package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.HARDNESS_UNBREAKABLE
import com.github.trc.clayium.api.util.next
import com.github.trc.clayium.common.config.ConfigCore
import com.github.trc.clayium.common.items.ItemClaySteelPickaxe.Mode.*
import com.github.trc.clayium.common.reflect.BlockReflect
import com.github.trc.clayium.common.util.UtilLocale
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Enchantments
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries

private const val SPEED_MULTIPLIER = 6

private fun List<BlockPos>.toIntArray(mapper: ((BlockPos) -> BlockPos)? = null): IntArray {
    val arr = IntArray(this.size * 2)
    for ((i, pos) in this.withIndex()) {
        val long = if (mapper != null) mapper(pos).toLong() else pos.toLong()
        val high = (long shr 32).toInt()
        val low = (long and 0xFFFFFFFF).toInt()
        arr[i * 2] = high
        arr[i * 2 + 1] = low
    }
    return arr
}

private fun IntArray.toBlockPosList(mapper: ((BlockPos) -> BlockPos)? = null): List<BlockPos> {
    val lst = IntArrayList(this)
    return lst.chunked(2).map { (high, low) ->
        val long = (high.toLong() shl 32) or (low.toLong() and 0xFFFFFFFF)
        val pos = BlockPos.fromLong(long)
        if (mapper != null) mapper(pos) else pos
    }
}

class ItemClaySteelPickaxe : ItemPickaxe(ToolMaterial.DIAMOND) {

    private val rangeBlock: IBlockState = run {
        if (ConfigCore.misc.claySteelToolBlock.isEmpty()) return@run Blocks.CLAY.defaultState

        val blockAndMeta = ConfigCore.misc.claySteelToolBlock.split(";")
        if (blockAndMeta.size == 1) {
            ForgeRegistries.BLOCKS.getValue(ResourceLocation(blockAndMeta[0]))?.defaultState
                ?: Blocks.CLAY.defaultState
        } else {
            val block = ForgeRegistries.BLOCKS.getValue(ResourceLocation(blockAndMeta[0]))
            val meta = blockAndMeta[1].toIntOrNull()
            @Suppress("DEPRECATION")
            if (block != null && meta != null) block.getStateFromMeta(meta) else Blocks.CLAY.defaultState
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack?> {
        if (playerIn.isSneaking) return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn))
        if (worldIn.isRemote) return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))

        val stack = playerIn.getHeldItem(handIn)
        val mode = getMode(stack)
        if (mode == null) {
            stack.tagCompound = NBTTagCompound().apply { setInteger("mode", SINGLE.ordinal) }
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
            val blockStack = ItemStack(rangeBlock.block, 1, rangeBlock.block.getMetaFromState(rangeBlock))
            if (!(player.canPlayerEdit(pos, facing, blockStack) && world.mayPlace(Blocks.CLAY, pos, false, facing, player))){
                return EnumActionResult.FAIL
            }

            if (world.setBlockState(pos, rangeBlock, Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
                val soundType = rangeBlock.block.getSoundType(rangeBlock, world, pos, player)
                world.playSound(player, pos, soundType.placeSound, SoundCategory.BLOCKS, (soundType.volume + 1f) / 2f, soundType.pitch * 0.8f)
            }
            return EnumActionResult.SUCCESS
        } else {
            val poses = getPoses(player, targetPos, 2)
                .filter { rangeBlock == world.getBlockState(it) }
            if (!world.isRemote) {
                val stack = player.getHeldItem(hand)
                if (!stack.hasTagCompound()) stack.tagCompound = NBTTagCompound()
                val arr = poses.toIntArray { it.subtract(targetPos) }
                stack.tagCompound!!.setIntArray("poses", arr)
            }
            return if (poses.isEmpty()) EnumActionResult.FAIL else EnumActionResult.SUCCESS
        }
    }

    override fun onBlockDestroyed(stack: ItemStack, worldIn: World, state: IBlockState, pos: BlockPos, entityLiving: EntityLivingBase): Boolean {
        if (worldIn.isRemote) return true
        val poses = when (getMode(stack)) {
            null, SINGLE -> { return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving) }
            RANGED -> getPoses(entityLiving, pos, 1)
            CUSTOM -> {
                stack.tagCompound?.getIntArray("poses")?.takeUnless { it.isEmpty() }
                    ?.toBlockPosList { pos.add(it) }
                    ?: getPoses(entityLiving, pos, 2)
            }
        }
        for (harvesting in poses) {
            if (harvesting == pos) continue
            val state = worldIn.getBlockState(harvesting)
            if (state.getBlockHardness(worldIn, harvesting) == HARDNESS_UNBREAKABLE) continue
            if (entityLiving is EntityPlayer
                && state.block.canSilkHarvest(worldIn, harvesting, state, entityLiving)
                && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0)
            {
                val drop = BlockReflect.getSilkTouchDrop(state.block, state)
                if (!drop.isEmpty) Block.spawnAsEntity(worldIn, harvesting, drop)
                worldIn.destroyBlock(harvesting, false)
            } else {
                worldIn.destroyBlock(harvesting, true)
            }
            stack.damageItem(1, entityLiving)
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

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val regName = this.registryName ?: return
        UtilLocale.formatTooltips(tooltip, "item.${regName.namespace}.${regName.path}.tooltip")
    }

    enum class Mode {
        SINGLE,
        RANGED,
        CUSTOM,
    }

    companion object {
        private var firstCall = true
        @SubscribeEvent
        fun onBreakSpeed(e: PlayerEvent.BreakSpeed) {
            // state.getPlayerRelativeBlockHardness fires this event
            if (!firstCall) return
            firstCall = false

            val stack = e.entityPlayer.getHeldItem(EnumHand.MAIN_HAND)
            val world = e.entityPlayer.world
            val item = stack.item
            if (item !is ItemClaySteelPickaxe) return
            if (!stack.hasTagCompound()) return
            val tag = stack.tagCompound!!
            val mode = Mode.entries[tag.getInteger("mode")]
            val poses = when (mode) {
                SINGLE -> return
                RANGED -> item.getPoses(e.entityPlayer, e.pos, 1)
                CUSTOM -> {
                    stack.tagCompound?.getIntArray("poses")?.takeUnless { it.isEmpty() }
                        ?.toBlockPosList { e.pos.add(it) }
                        ?: item.getPoses(e.entityPlayer, e.pos, 2)
                }
            }
            var hardness = 0f
            for (pos in poses) {
                val state = world.getBlockState(pos)
                if (state.material == Material.AIR) continue
                val relHardness = state.getPlayerRelativeBlockHardness(e.entityPlayer, world, pos) * 30f
                hardness += if (relHardness == 0.0f) Float.POSITIVE_INFINITY else 1.0f / relHardness
            }
            e.newSpeed = if (hardness == 0f) Float.POSITIVE_INFINITY else 1f / hardness
            firstCall = true
        }
    }
}
