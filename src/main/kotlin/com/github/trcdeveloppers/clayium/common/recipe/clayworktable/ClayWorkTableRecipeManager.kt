package com.github.trcdeveloppers.clayium.common.recipe.clayworktable

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdeveloppers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod.Companion.fromId
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.item.ItemStack
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.ForgeRegistries
import org.apache.commons.io.FilenameUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object ClayWorkTableRecipeManager {
    val INSTANCE = this

    private val recipes: MutableList<ClayWorkTableRecipe> = ArrayList()

    init {
        CraftingHelper.findFiles(
            Loader.instance().indexedModList["clayium"],
            "assets/clayium/machine_recipes/clay_work_table/",
            null,
            { root: Path, file: Path ->
                val relative = root.relativize(file).toString()
                if ("json" != FilenameUtils.getExtension(file.toString()) || relative.startsWith("_")) return@findFiles true
                val name = FilenameUtils.removeExtension(relative).replace("\\\\".toRegex(), "/")
                val key = ResourceLocation(Clayium.MOD_ID, "clay_work_table.$name")
                try {
                    Files.newBufferedReader(file).use { reader ->
                        val json = JsonUtils.fromJson(CraftingHelper.GSON, reader, JsonObject::class.java) ?: return@use
                        recipes.add(getRecipe(json) ?: run { Clayium.LOGGER.error("Invalid recipe found at {}", key); return@findFiles true })
                    }
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
                true
            },
            true,
            true
        )
    }

    /**
     * @param input can be [ItemStack.EMPTY], in which case it always returns [Optional.empty].
     */
    fun getRecipeFor(input: ItemStack, method: ClayWorkTableMethod): ClayWorkTableRecipe? {
        if (input.isEmpty) {
            return null
        }

        for (recipe in recipes) {
            if (recipe.matches(input, method)) {
                return recipe
            }
        }
        return null
    }

    private fun getRecipe(json: JsonObject): ClayWorkTableRecipe? {
        if (json.isJsonNull) {
            throw JsonSyntaxException("json cannot be null")
        }
        if (!(json.has("input") && json.has("output") && json.has("method") && json.has("clicks"))) {
            return null
        }

        val inputJson = json.getAsJsonObject("input") ?: return null
        if (!(inputJson.has("item") && inputJson.has("amount"))) {
            return null
        }
        val inputItem = ForgeRegistries.ITEMS.getValue(ResourceLocation(inputJson["item"].asString))
            ?: run {
                Clayium.LOGGER.error("ClayWorkTable Recipe: input item not found")
                return null
            }

        val outputJson = json.getAsJsonObject("output") ?: return null
        if (!(outputJson.has("item") && outputJson.has("amount"))) {
            return null
        }

        val outputItem = ForgeRegistries.ITEMS.getValue(ResourceLocation(outputJson["item"].asString))
        if (outputItem == null) {
            Clayium.LOGGER.error("ClayWorkTable Recipe: output item not found")
            return null
        }
        val secondaryOutputJson = json.getAsJsonObject("output_2") ?: null
        val secondaryOutputItem = if (secondaryOutputJson == null) {
            null
        } else {
            ForgeRegistries.ITEMS.getValue(ResourceLocation(secondaryOutputJson["item"].asString))
                ?: run {
                    Clayium.LOGGER.error("ClayWorkTable Recipe: secondary output item not found")
                    return null
                }
        }

        val method = fromId(json["method"].asInt)
            ?: run {
                Clayium.LOGGER.error("ClayWorkTable Recipe: method ID must be in the range 1-6 but got {}", json["method"].asInt)
                return null
            }
        val clicks = json["clicks"].asInt
        return ClayWorkTableRecipe(
            ItemStack(inputItem, inputJson["amount"].asInt),
            ItemStack(outputItem, outputJson["amount"].asInt),
            if (secondaryOutputItem == null) ItemStack.EMPTY else ItemStack(secondaryOutputItem, secondaryOutputJson!!["amount"].asInt),
            method, clicks
        )
    }
}
