package com.github.trc.clayium.datafix

import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.datafix.fixable.MteHolderMigrator
import net.minecraft.util.datafix.FixTypes
import net.minecraftforge.fml.common.FMLCommonHandler

object ClayiumDataFix {
    fun init() {
        val globalFixer = FMLCommonHandler.instance().dataFixer
        val modFixs = globalFixer.init(MOD_ID, ClayiumDataVersion.currentVersion.ordinal)
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, MteHolderMigrator())
    }
}