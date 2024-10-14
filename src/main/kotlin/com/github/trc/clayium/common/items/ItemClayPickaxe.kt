package com.github.trc.clayium.common.items

import com.github.trc.clayium.common.blocks.ores.IClayOreBlock
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import javax.annotation.ParametersAreNonnullByDefault

class ItemClayPickaxe : ItemPickaxe(ToolMaterial.STONE) {
    private val efficiencyOnClayOre = 32.0f

    init {
        creativeTab = ClayiumCTabs.main
        maxDamage = 500
    }

    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab)) {
            items.add(ItemStack(this, 1, 0))
        }
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        if (state.block is IClayOreBlock) {
            val blockHarvestLevel = state.block.getHarvestLevel(state)
            val itemHarvestLevel =
                stack.item.getHarvestLevel(
                    stack,
                    state.block.getHarvestTool(state) ?: "",
                    null,
                    state
                )
            return if (blockHarvestLevel <= itemHarvestLevel) {
                efficiencyOnClayOre
            } else {
                efficiencyOnClayOre * 100f / 30f
            }
        }
        return super.getDestroySpeed(stack, state)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        tooltip.add(I18n.format("item.clayium.clay_pickaxe.tooltip"))
    }
}
