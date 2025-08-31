package io.github.trcdevelopers.clayium.integration.groovy

import com.cleanroommc.groovyscript.api.GroovyLog
import com.cleanroommc.groovyscript.helper.Alias
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import com.google.common.base.CaseFormat
import io.github.trcdevelopers.clayium.api.W
import io.github.trcdevelopers.clayium.common.recipe.LaserRecipe
import io.github.trcdevelopers.clayium.common.recipe.builder.LaserRecipeBuilder
import io.github.trcdevelopers.clayium.common.recipe.registry.LaserRecipeRegistry
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState

class LaserRecipeRegistryGrsAdapter(
    val backingRegistry: LaserRecipeRegistry,
) : VirtualizedRegistry<LaserRecipe>(Alias.generateOf("laser_transformation", CaseFormat.LOWER_UNDERSCORE)) {
    override fun onReload() {
        removeScripted().forEach(backingRegistry::removeRecipe)
        restoreFromBackup().forEach(backingRegistry::addRecipe)
    }

    fun recipeBuilder(): LaserRecipeBuilder {
        return LaserRecipeBuilder(backingRegistry)
    }

    override fun getName(): String? {
        return "laser_transformation"
    }
    @JvmOverloads
    fun removeRecipe(input: Block, inputMeta: Int = W): Boolean {
        return backingRegistry.getAllRecipes().filter { it.grsMatches(input, inputMeta) }
            .map { recipe ->
                val removed = backingRegistry.removeRecipe(recipe)
                if (!removed) GroovyLog.msg("Failed to remove recipe $recipe")
                removed
            }
            .all { it }
    }
    fun removeRecipe(input: IBlockState): Boolean {
        return removeRecipe(input.block, input.block.getMetaFromState(input))
    }
}