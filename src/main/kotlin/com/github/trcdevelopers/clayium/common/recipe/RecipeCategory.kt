package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.Clayium
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

class RecipeCategory private constructor(
    val modid: String = Clayium.MOD_ID,
    val categoryName: String,
    val translationKey: String,
) {
    val uniqueId = createUID(modid, categoryName)

    companion object {
        val categories = Object2ObjectOpenHashMap<String, RecipeCategory>()
        fun create(modid: String, categoryName: String, translationKey: String): RecipeCategory {
            return categories.computeIfAbsent(createUID(modid, categoryName)) { _ ->
                RecipeCategory(modid, categoryName, translationKey)
            }
        }

        private fun createUID(modid: String, name: String): String {
            return "$modid/$name"
        }
    }
}
