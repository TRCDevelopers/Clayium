package io.github.trcdevelopers.clayium.integration.groovy

import com.cleanroommc.groovyscript.api.GroovyLog
import com.cleanroommc.groovyscript.helper.Alias
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import com.google.common.base.CaseFormat
import io.github.trcdevelopers.clayium.common.recipe.Recipe
import io.github.trcdevelopers.clayium.common.recipe.builder.RecipeBuilder
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.item.ItemStack

class RecipeRegistryGrsAdapter(
    val backingRegistry: RecipeRegistry<*>,
) : VirtualizedRegistry<Recipe>(Alias.generateOf(backingRegistry.categoryName, CaseFormat.LOWER_UNDERSCORE)) {
    override fun onReload() {
        removeScripted().forEach(backingRegistry::removeRecipe)
        restoreFromBackup().forEach(backingRegistry::addRecipe)
    }

    fun recipeBuilder(): RecipeBuilder<*> {
        return backingRegistry.builder()
    }

    override fun getName(): String {
        return backingRegistry.categoryName
    }

    fun removeRecipe(inputs: List<ItemStack>, tier: Int): Boolean {
        return backingRegistry.getAllRecipes().filter { it.matches(inputs, tier) }
            .map { recipe ->
                val removed = backingRegistry.removeRecipe(recipe)
                if (!removed) GroovyLog.msg("Failed to remove recipe $recipe")
                removed
            }
            .all { it }
    }
}