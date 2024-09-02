package com.github.trc.clayium.integration.groovy

import com.cleanroommc.groovyscript.helper.Alias
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.builder.RecipeBuilder
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import com.google.common.base.CaseFormat

class RecipeRegistryGrsAdapter(
    val backingRegistry: RecipeRegistry<*>,
) : VirtualizedRegistry<Recipe>(Alias.generateOf(backingRegistry.categoryName, CaseFormat.LOWER_UNDERSCORE)) {
    override fun onReload() {
        //todo
    }

    fun recipeBuilder(): RecipeBuilder<*> {
        return backingRegistry.builder()
    }

    override fun getName(): String {
        return backingRegistry.categoryName
    }
}