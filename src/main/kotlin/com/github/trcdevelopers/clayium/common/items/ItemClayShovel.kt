package com.github.trcdevelopers.clayium.common.items

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.annotation.CItem
import com.github.trcdevelopers.clayium.common.blocks.ores.IClayOreBlock
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemSpade
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@CItem(registryName = "clay_shovel")
class ItemClayShovel : ItemSpade(ToolMaterial.WOOD) {
    private val efficiencyOnClayOre = 12.0f
    private val efficiencyOnClayBlocks = 32.0f

    init {
        creativeTab = Clayium.creativeTab
        maxDamage = 500
    }

    @Suppress("DEPRECATION")
    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val block = state.block
        if (block.getMaterial(state) === Material.CLAY) {
            return efficiencyOnClayBlocks
        }
        if (block is IClayOreBlock) {
            return efficiencyOnClayOre
        }
        return super.getDestroySpeed(stack, state)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if (registryName == null) {
            return
        }
        val list = UtilLocale.localizeTooltip(
            "item." + registryName!!
                .path + ".tooltip"
        )
        if (list != null) {
            tooltip.addAll(list)
        }
    }
}
