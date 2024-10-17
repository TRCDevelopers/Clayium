package com.github.trc.clayium.common.blocks.ores

import com.github.trc.clayium.common.items.ItemClayPickaxe
import com.github.trc.clayium.common.items.ItemClayShovel
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.event.ForgeEventFactory
import java.util.Random

class BlockClayOre : Block(Material.ROCK), IClayOreBlock {
    init {
        setSoundType(SoundType.STONE)
        setLightLevel(0f)
        setResistance(5f)
        setHardness(3f)
        setHarvestLevel("pickaxe", 1)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Items.CLAY_BALL
    }

    override fun quantityDroppedWithBonus(fortune: Int, random: Random): Int {
        if (fortune > 0) {
            var i = random.nextInt(fortune + 2) - 1
            if (i < 0) {
                i = 0
            }
            return this.quantityDropped(random) * (i + 1)
        }
        return this.quantityDropped(random)
    }

    override fun quantityDropped(random: Random): Int {
        return 4 + random.nextInt(5) * random.nextInt(4)
    }

    override fun canHarvestBlock(
        world: IBlockAccess,
        pos: BlockPos,
        player: EntityPlayer
    ): Boolean {
        return (player.heldItemMainhand.item is ItemClayShovel) ||
            super.canHarvestBlock(world, pos, player)
    }

    override fun harvestBlock(
        worldIn: World,
        player: EntityPlayer,
        pos: BlockPos,
        state: IBlockState,
        te: TileEntity?,
        stack: ItemStack
    ) {
        StatList.getBlockStats(this)?.let { player.addStat(it) }
        player.addExhaustion(0.005f)
        if (
            this.canSilkHarvest(worldIn, pos, state, player) &&
                EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0
        ) {
            val items: MutableList<ItemStack> = ArrayList()
            val itemStack = getSilkTouchDrop(state)
            if (!itemStack.isEmpty) {
                items.add(itemStack)
            }
            ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player)
            for (item in items) {
                spawnAsEntity(worldIn, pos, item)
            }
        } else {
            harvesters.set(player)
            var i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack)
            // ClayShovel: +3 fortune, ClayPickaxe: +4 fortune.
            if (player.heldItemMainhand.item is ItemClayPickaxe) {
                i = (i + 1) * 4
            } else if (player.heldItemMainhand.item is ItemClayShovel) {
                i = (i + 1) * 3
            }
            dropBlockAsItem(worldIn, pos, state, i)
            harvesters.remove()
        }
    }
}
