package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.blocks.machine.clayworktable.ClayWorkTableMethod
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.CRecipes
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.init.Items

object ClayWorkTableRecipes {
    fun register() {
        CRecipes.CLAY_WORK_TABLE.register {
            input(Items.CLAY_BALL, 1)
            primaryOutput(MetaItemClayParts.CLAY_STICK.stackForm)
            method(ClayWorkTableMethod.ROLLING_HAND)
            clicks(4)
        }
        CRecipes.CLAY_WORK_TABLE.register {
            input(MetaItemClayParts.CLAY_DISC)
            primaryOutput(OrePrefix.PLATE, Material.CLAY)
            method(ClayWorkTableMethod.CUT_DISC)
            clicks(4)
        }
    }
}