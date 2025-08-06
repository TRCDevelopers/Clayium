package io.github.trcdevelopers.clayium.datafix

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.datafix.fixable.ClayMarkerHandlerMigrator
import io.github.trcdevelopers.clayium.datafix.fixable.MteHolderMigrator
import net.minecraft.util.datafix.FixTypes
import net.minecraftforge.fml.common.FMLCommonHandler

object ClayiumDataFix {
    fun init() {
        val globalFixer = FMLCommonHandler.instance().dataFixer
        val modFixs = globalFixer.init(MOD_ID, ClayiumDataVersion.currentVersion.ordinal)
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, MteHolderMigrator())
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, ClayMarkerHandlerMigrator())
    }
}