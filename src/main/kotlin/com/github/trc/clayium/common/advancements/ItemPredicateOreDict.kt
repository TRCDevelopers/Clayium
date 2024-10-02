package com.github.trc.clayium.common.advancements

import com.github.trc.clayium.api.unification.OreDictUnifier
import com.google.gson.JsonArray
import com.google.gson.JsonElement
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
            val oreDictName = JsonUtils.getString(element, "oredict")
            if (oreDictName.isEmpty()) return null
            return ItemPredicateOreDict(oreDictName)
        }

        fun deserializeArray(element: JsonElement?): List<ItemPredicateOreDict> {
            if (element == null || element.isJsonNull) return emptyList()
            val jsonArray: JsonArray = JsonUtils.getJsonArray(element, "items")
            return jsonArray.map(::deserialize).filterNotNull()
        }
    }
}