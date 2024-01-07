package com.github.trcdeveloppers.clayium.common.items

import com.github.trcdeveloppers.clayium.common.Clayium.Companion.MOD_ID
import net.minecraft.client.renderer.block.model.ModelResourceLocation

enum class CShape {
    INGOT,
    PLATE,
    LARGE_PLATE,
    DUST,
    MATTER,
    MATTER2,
    MATTER3,
    MATTER4,
    MATTER5;

    val model = ModelResourceLocation("$MOD_ID:colored/${name.lowercase()}", "inventory")

    companion object {
        val INGOT_BASED = arrayOf(INGOT, PLATE, LARGE_PLATE, DUST)
        val MATTER_BASED = arrayOf(MATTER, PLATE, LARGE_PLATE, DUST)
        val INGOT_DUST = arrayOf(INGOT, DUST)
    }
}
