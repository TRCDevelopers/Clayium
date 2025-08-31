package io.github.trcdevelopers.clayium.common.pan.factories

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.metatileentity.multiblock.ClayReactorMetaTileEntity
import io.github.trcdevelopers.clayium.api.pan.IPanRecipe
import io.github.trcdevelopers.clayium.api.pan.IPanRecipeFactory
import io.github.trcdevelopers.clayium.api.util.getMetaTileEntity
import io.github.trcdevelopers.clayium.common.pan.PanRecipe
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import kotlin.math.ceil

object CPanRecipeFactory : IPanRecipeFactory {
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        val metaTileEntity = world.getMetaTileEntity(pos) ?: return null

        val multiblockCapability = metaTileEntity.getCapability(ClayiumTileCapabilities.MULTIBLOCK, null)
        var machineTier = metaTileEntity.tier.numeric

        if (multiblockCapability != null) {
            if (!multiblockCapability.structureFormed) return null
            if (metaTileEntity is ClayReactorMetaTileEntity) return getEntryClayReactor(metaTileEntity, stacks, laserEnergy, laserCostPerTick)
            machineTier = multiblockCapability.recipeLogicTier
        }
        val recipe = metaTileEntity
            .getCapability(ClayiumTileCapabilities.RECIPE_LOGIC, null)
            ?.recipeProvider
            ?.searchRecipe(machineTier, stacks)
            ?: return null

        return PanRecipe(recipe.inputs, recipe.copyOutputs(), recipe.cePerTick * recipe.duration)
    }

    private fun getEntryClayReactor(clayReactor: ClayReactorMetaTileEntity, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        val recipe = clayReactor.workable.recipeProvider.searchRecipe(Int.MAX_VALUE, stacks) ?: return null

        // TODO: Ignore white laser
        val finalizedDuration = ceil(recipe.duration.toDouble() / (laserEnergy + 1.0)).toLong()
        return PanRecipe(recipe.inputs, recipe.copyOutputs(), (recipe.cePerTick + laserCostPerTick) * finalizedDuration)
    }
}