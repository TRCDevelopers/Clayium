package io.github.trcdevelopers.clayium.common.metatileentities

import io.github.trcdevelopers.clayium.api.metatileentity.AbstractItemGeneratorMetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.clayiumId
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

class CobblestoneGeneratorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractItemGeneratorMetaTileEntity(
    metaTileEntityId, tier,
    validInputModes = onlyNoneList, validOutputModes = validOutputModesLists[1],
    "cobblestone_generator") {

    override val generatingItem: ItemStack = ItemStack(Blocks.COBBLESTONE)
    override val progressPerItem = 100
    override val progressPerTick = when (tier.numeric) {
        1 -> 2
        2 -> 5
        3 -> 15
        4 -> 50
        5 -> 200
        6 -> 1000
        7 -> 8000
        else -> 1
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return CobblestoneGeneratorMetaTileEntity(this.metaTileEntityId, this.tier)
    }

    override fun isTerrainValid(): Boolean {
        var hasWaterNeighbor = false
        var hasLavaNeighbor = false
        for (side in EnumFacing.entries) {
            val neighborMaterial = getNeighborBlockState(side)?.material
            hasWaterNeighbor = hasWaterNeighbor || neighborMaterial == Material.WATER
            hasLavaNeighbor = hasLavaNeighbor || neighborMaterial == Material.LAVA

            if (hasWaterNeighbor && hasLavaNeighbor) return true
        }
        return false
    }

    override val renderingConfig: MteRenderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/cobblestone_generator"))
    }
}