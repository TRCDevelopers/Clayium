package com.github.trcdevelopers.clayium.common.blocks

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.properties.CMaterialProperty
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.block.SoundType
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class BlockEnergizedClay(
    mapping: Map<Int, Material>,
) : BlockMaterialBase(net.minecraft.block.material.Material.GROUND, mapping) {

    init {
        setSoundType(SoundType.GROUND)
        setHarvestLevel("shovel", 0)
        setHardness(0.6f)

        setTranslationKey("energized_clay")
        setCreativeTab(Clayium.creativeTab)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        val energizedClayCapability = stack.getCapability(ClayiumCapabilities.ENERGIZED_CLAY, null)
        if (energizedClayCapability != null) tooltip.add(energizedClayCapability.getClayEnergy().format())
    }

    companion object {
        fun create(mapping: Map<Int, Material>): BlockEnergizedClay {
            val materials = mapping.values
            val prop = CMaterialProperty(materials, "material")
            return object : BlockEnergizedClay(mapping) {
                override fun getMaterialProperty() = prop
            }
        }
    }
}