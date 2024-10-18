package com.github.trc.clayium.common.util

import com.github.trc.clayium.api.util.CUtils

/**
 * Please use it only in places where it is also called from the server. uses
 * [net.minecraft.client.resources.I18n] on the client side. which is the correct way to do it. uses
 * [net.minecraft.util.text.translation.I18n] on the server side, which is deprecated.
 *
 * **use [net.minecraft.client.resources.I18n] or [net.minecraft.util.text.TextComponentTranslation]
 * whenever possible.**
 */
@Suppress("DEPRECATION")
object BothSideI18n {
    fun format(key: String, vararg args: Any): String {
        return if (CUtils.isClientSide) {
            net.minecraft.client.resources.I18n.format(key, *args)
        } else {
            net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, *args)
        }
    }

    fun hasKey(key: String): Boolean {
        return if (CUtils.isClientSide) {
            net.minecraft.client.resources.I18n.hasKey(key)
        } else {
            net.minecraft.util.text.translation.I18n.canTranslate(key)
        }
    }
}
