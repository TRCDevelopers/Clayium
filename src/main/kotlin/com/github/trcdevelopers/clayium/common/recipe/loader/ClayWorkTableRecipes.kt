package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod.CUT
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod.CUT_DISC
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod.CUT_PLATE
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod.PUNCH
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod.ROLLING_HAND
import com.github.trcdevelopers.clayium.common.blocks.clayworktable.ClayWorkTableMethod.ROLLING_PIN
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_BLADE
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_CYLINDER
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_DISC
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_NEEDLE
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_RING
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_SHORT_STICK
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_SMALL_DISC
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_SMALL_RING
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.CLAY_STICK
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts.LARGE_CLAY_BALL
import com.github.trcdevelopers.clayium.common.recipe.CWTRecipes
import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.OrePrefix.LARGE_PLATE
import com.github.trcdevelopers.clayium.common.unification.OrePrefix.PLATE
import com.github.trcdevelopers.clayium.common.unification.material.Material.CLAY
import net.minecraft.init.Items
import net.minecraft.item.ItemStack


object ClayWorkTableRecipes {
    fun register() {
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(CLAY_CYLINDER)
            outputs(CLAY_NEEDLE.getStackForm(1))
            method(ROLLING_HAND)
            clicks(3)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(Items.CLAY_BALL)
            outputs(CLAY_STICK.getStackForm(1))
            method(ROLLING_HAND)
            clicks(4)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(LARGE_CLAY_BALL)
            outputs(CLAY_CYLINDER.getStackForm(1))
            method(ROLLING_HAND)
            clicks(5)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(PLATE, CLAY, 3)
            outputs(LARGE_CLAY_BALL.getStackForm(1))
            method(ROLLING_HAND)
            clicks(40)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(PLATE, CLAY)
            outputs(CLAY_BLADE.getStackForm(1))
            method(PUNCH)
            clicks(10)
        }
//        CWTRecipes.CLAY_WORK_TABLE.register {
//            input(CLAY_DISC)
//            outputs(/* raw clay slicer */)
//            method(PUNCH)
//            clicks(15)
//        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(LARGE_CLAY_BALL)
            outputs(CLAY_DISC.getStackForm(1))
            method(PUNCH)
            clicks(30)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(PLATE, CLAY)
            outputs(CLAY_BLADE.getStackForm(1), ItemStack(Items.CLAY_BALL, 2))
            method(ROLLING_PIN)
            clicks(1)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(LARGE_CLAY_BALL)
            outputs(CLAY_DISC.getStackForm(1), ItemStack(Items.CLAY_BALL, 2))
            method(ROLLING_PIN)
            clicks(4)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(PLATE, CLAY, 6)
            outputs(OreDictUnifier.get(LARGE_PLATE, CLAY, 1))
            method(ROLLING_PIN)
            clicks(10)
        }
//        CWTRecipes.CLAY_WORK_TABLE.register {
//            input(CLAY_DISC)
//            outputs(/* raw clay slicer */)
//            method(ROLLING_PIN)
//            clicks(2)
//        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(CLAY_DISC)
            outputs(OreDictUnifier.get(PLATE, CLAY), ItemStack(Items.CLAY_BALL, 2))
            method(CUT_PLATE)
            clicks(4)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(CLAY_SMALL_DISC)
            outputs(CLAY_SMALL_RING.getStackForm(1), CLAY_SHORT_STICK.getStackForm(1))
            method(CUT_DISC)
            clicks(1)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(CLAY_DISC)
            outputs(CLAY_RING.getStackForm(1), CLAY_SMALL_DISC.getStackForm(1))
            method(CUT_DISC)
            clicks(2)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(PLATE, CLAY)
            outputs(CLAY_STICK.getStackForm(4))
            method(CUT)
            clicks(3)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(CLAY_CYLINDER)
            outputs(CLAY_SMALL_DISC.getStackForm(8))
            method(CUT)
            clicks(7)
        }
    }
}