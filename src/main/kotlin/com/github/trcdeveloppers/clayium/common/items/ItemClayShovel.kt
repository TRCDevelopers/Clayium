package com.github.trcdeveloppers.clayium.common.items

import com.github.trcdeveloppers.clayium.common.Clayium
import com.github.trcdeveloppers.clayium.common.annotation.CItem
import com.github.trcdeveloppers.clayium.common.util.UtilLocale
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
    private var efficiencyOnClayBlocks = 32.0f

    init {
        creativeTab = Clayium.CreativeTab
        maxDamage = 500
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        if (state.block.getMaterial(state) === Material.CLAY) {
            return efficiencyOnClayBlocks
        }
        if (state.block.registryName != null) {
            val regName = state.block.registryName.toString()
            if (regName.startsWith("clayium") && regName.endsWith("clay_ore")) {
                return efficiencyOnClayOre
            }
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
