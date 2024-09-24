package com.github.trc.clayium.common.recipe

import com.github.trc.clayium.api.MOD_ID
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

class RecipeCategory private constructor(
    val modid: String = MOD_ID,
    val categoryName: String,
) {
    val uniqueId = createUID(modid, categoryName)
    val translationKey = "recipe.clayium.$categoryName"

    companion object {
        val categories = Object2ObjectOpenHashMap<String, RecipeCategory>()
        fun create(modid: String, categoryName: String): RecipeCategory {
            return categories.computeIfAbsent(createUID(modid, categoryName)) { _ ->
                RecipeCategory(modid, categoryName)
            }
        }

        private fun createUID(modid: String, name: String): String {
            if (modid.isEmpty() || name.isEmpty())
                throw IllegalArgumentException("modid and name must not be empty.")
            return "$modid.$name"
        }
    }
}
