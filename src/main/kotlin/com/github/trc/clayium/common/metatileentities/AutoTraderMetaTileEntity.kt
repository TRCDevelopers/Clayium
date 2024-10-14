package com.github.trc.clayium.common.metatileentities

import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.recipe.IRecipeProvider
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.common.recipe.Recipe
import net.minecraft.entity.IMerchant
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB

class AutoTraderMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity() {

    override fun update() {
        super.update()
        if (isRemote || offsetTimer % 5 != 0L) return
        val world = world ?: return
        val pos = pos ?: return

        val entities = world.getEntitiesWithinAABB(EntityVillager::class.java, AxisAlignedBB(pos.add(1, 3, 1)))
        if (entities.isEmpty()) return
    }

    private inner class AutoTraderRecipeProvider(merchant: IMerchant) : IRecipeProvider {
        override val jeiCategory: String? = null

        override fun searchRecipe(machineTier: Int, inputs: List<ItemStack>): Recipe? {
            TODO("Not yet implemented")
        }
    }
}