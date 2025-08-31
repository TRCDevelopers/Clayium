package io.github.trcdevelopers.clayium.common.util

import io.github.trcdevelopers.clayium.api.util.CUtils

/**
 * Please use it only in places where it is also called from the server, or loaded on the server.
 * For example, if you use client-only I18n in `IKey.dynamic`, it will throw a ClassNotFoundException on a dedicated server.
 * So you should use this in such a situation.
 *
 * This uses [net.minecraft.client.resources.I18n] on the client side, which is the correct way to do it.
 * and [net.minecraft.util.text.translation.I18n] on the server side, which is deprecated.
 *
 * **use [net.minecraft.client.resources.I18n] or [net.minecraft.util.text.TextComponentTranslation] whenever possible.**
 */
@Suppress("DEPRECATION")
object SidelessI18n {
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