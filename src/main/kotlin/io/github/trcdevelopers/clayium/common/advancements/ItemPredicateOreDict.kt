package io.github.trcdevelopers.clayium.common.advancements

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import net.minecraft.item.ItemStack
import net.minecraft.util.JsonUtils

class ItemPredicateOreDict(
    val oreDict: String,
) {

    fun test(stack: ItemStack): Boolean {
        return OreDictUnifier.getOreNames(stack)
            .contains(oreDict)
    }

    companion object {
        fun deserialize(element: JsonElement?): ItemPredicateOreDict? {
            if (element == null || element.isJsonNull) return null
            val oreDictName = element.asString
            if (oreDictName.isEmpty()) return null
            return ItemPredicateOreDict(oreDictName)
        }

        fun deserializeArray(element: JsonElement?): List<ItemPredicateOreDict> {
            if (element == null || element.isJsonNull) return emptyList()
            val jsonArray: JsonArray = JsonUtils.getJsonArray(element, "items")
            return jsonArray.mapNotNull(::deserialize)
        }
    }
}