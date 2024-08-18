package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.unification.material.CMaterials.clay
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod.CUT
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod.CUT_DISC
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod.CUT_PLATE
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod.PUNCH
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod.ROLLING_HAND
import com.github.trc.clayium.common.blocks.clayworktable.ClayWorkTableMethod.ROLLING_PIN
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts.LargeClayBall
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts.RawClaySlicer
import com.github.trc.clayium.common.recipe.CWTRecipes
import net.minecraft.init.Items
import net.minecraft.item.ItemStack


object ClayWorkTableRecipes {
    fun registerRecipes() {
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.cylinder, clay)
            output(OrePrefix.needle, clay)
            method(ROLLING_HAND)
            clicks(3)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(Items.CLAY_BALL)
            output(OrePrefix.stick, clay)
            method(ROLLING_HAND)
            clicks(4)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(LargeClayBall)
            output(OrePrefix.cylinder, clay)
            method(ROLLING_HAND)
            clicks(5)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.plate, clay, 3)
            output(LargeClayBall)
            method(ROLLING_HAND)
            clicks(40)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.largePlate, clay)
            output(OrePrefix.blade, clay)
            method(PUNCH)
            clicks(10)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.disc, clay)
            output(RawClaySlicer)
            method(PUNCH)
            clicks(15)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(LargeClayBall)
            output(OrePrefix.disc, clay)
            method(PUNCH)
            clicks(30)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.plate, clay)
            output(OrePrefix.blade, clay)
            output(ItemStack(Items.CLAY_BALL, 2))
            method(ROLLING_PIN)
            clicks(1)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(LargeClayBall)
            output(OrePrefix.disc, clay)
            output(ItemStack(Items.CLAY_BALL, 2))
            method(ROLLING_PIN)
            clicks(4)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.plate, clay, 6)
            output(OrePrefix.largePlate, clay)
            method(ROLLING_PIN)
            clicks(10)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.disc, clay)
            output(RawClaySlicer)
            method(ROLLING_PIN)
            clicks(2)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.disc, clay)
            output(OrePrefix.plate, clay)
            output(ItemStack(Items.CLAY_BALL, 2))
            method(CUT_PLATE)
            clicks(4)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.smallDisc, clay)
            output(OrePrefix.smallRing, clay)
            output(OrePrefix.shortStick, clay)
            method(CUT_DISC)
            clicks(1)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.disc, clay)
            output(OrePrefix.ring, clay)
            output(OrePrefix.smallDisc, clay)
            method(CUT_DISC)
            clicks(2)
        }

        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.plate, clay)
            output(OrePrefix.stick, clay, 4)
            method(CUT)
            clicks(3)
        }
        CWTRecipes.CLAY_WORK_TABLE.register {
            input(OrePrefix.cylinder, clay)
            output(OrePrefix.smallDisc, clay, 8)
            method(CUT)
            clicks(7)
        }
    }
}