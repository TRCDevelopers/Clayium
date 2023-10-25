package com.github.trcdeveloppers.clayium.common.items

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.common.annotation.CItem
import com.github.trcdeveloppers.clayium.common.util.UtilLocale
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import javax.annotation.ParametersAreNonnullByDefault

@CItem(registryName = "clay_pickaxe")
class ItemClayPickaxe : ItemPickaxe(ToolMaterial.STONE) {
    private val efficiencyOnClayOre = 32.0f

    init {
        creativeTab = Clayium.CreativeTab
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
        if (state.block.registryName != null) {
            val regName = state.block.registryName.toString()
            if (regName.startsWith("clayium") && regName.endsWith("clay_ore")) {
                val blockHarvestLevel = state.block.getHarvestLevel(state)
                val itemHarvestLevel = stack.item.getHarvestLevel(stack, state.block.getHarvestTool(state) ?: "", null, state)
                return if (blockHarvestLevel <= itemHarvestLevel) {
                    efficiencyOnClayOre
                } else {
                    efficiencyOnClayOre * 100f / 30f
                }
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
