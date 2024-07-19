package com.github.trc.clayium.common.pan.factories

import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.pan.IPanEntry
import com.github.trc.clayium.api.pan.IPanEntryFactory
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.pan.PanEntry
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object CPanEntryFactory : IPanEntryFactory {
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>): IPanEntry? {
        val recipe = world.getMetaTileEntity(pos)?.getCapability(ClayiumTileCapabilities.RECIPE_LOGIC, null)
            ?.recipeRegistry?.findRecipe(Int.MAX_VALUE, stacks) ?: return null

        return PanEntry(recipe.inputs, recipe.copyOutputs(), recipe.cePerTick * recipe.duration)
    }

}