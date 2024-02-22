package com.github.trcdevelopers.clayium.common.blocks.clay

import com.github.trcdevelopers.clayium.common.ClayEnergy
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.util.I18nUtils
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.pow

class ItemBlockEnergizedClay(block: BlockEnergizedClay) : ItemBlock(block) {

    init {
        hasSubtypes = true
        creativeTab = Clayium.creativeTab
        registryName = block.registryName
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return I18nUtils.format("item.energized_clay_${stack.metadata}.name")
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18nUtils.format("tooltip.tier", stack.metadata))
        tooltip.add(I18nUtils.format("tooltip.ce", 0))
    }

    fun getClayEnergy(stack: ItemStack): ClayEnergy {
        return ClayEnergy.of(10.toDouble().pow(stack.metadata).toLong())
    }
}