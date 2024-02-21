package com.github.trcdevelopers.clayium.common.util

import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.client.resources.I18n

object I18nUtils {
    fun format(key: String, vararg args: Any): String {
        return I18n.format("${Clayium.MOD_ID}.$key", *args)
    }
}