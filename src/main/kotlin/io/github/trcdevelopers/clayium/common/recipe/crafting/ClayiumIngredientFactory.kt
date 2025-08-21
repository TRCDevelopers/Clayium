package io.github.trcdevelopers.clayium.common.recipe.crafting

import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreIngredient

object ClayiumIngredientFactory {

    fun get(nbt: Boolean, obj: Any): Ingredient {
        return if (nbt) {
            getNbtSensitive(obj)
        } else {
            get(obj)
        }
    }

    fun getNbtSensitive(obj: Any): Ingredient {
        return when (obj) {
            is Ingredient -> obj
            is ItemStack -> CIngredientNBT(obj)
            is Item -> CIngredientNBT(ItemStack(obj))
            is Block -> CIngredientNBT(ItemStack(obj))
            is MetaItemClayium.MetaValueItem -> CIngredientNBT(obj.getStackForm())
            is MetaTileEntity -> CIngredientNBT(obj.asStackForm())
            else -> throw IllegalArgumentException("Unsupported ingredient type: ${obj::class.java.name}")
        }
    }

    fun get(obj: Any): Ingredient {
        return when (obj) {
            is Ingredient -> obj
            is ItemStack -> Ingredient.fromStacks(obj)
            is Item -> Ingredient.fromStacks(ItemStack(obj))
            is Block -> Ingredient.fromStacks(ItemStack(obj))

            is MetaItemClayium.MetaValueItem -> Ingredient.fromStacks(obj.getStackForm())
            is MetaTileEntity -> Ingredient.fromStacks(obj.asStackForm())
            is UnificationEntry -> OreIngredient(obj.oreName)
            else -> throw IllegalArgumentException("Unsupported ingredient type: ${obj::class.java.name}")
        }
    }
}