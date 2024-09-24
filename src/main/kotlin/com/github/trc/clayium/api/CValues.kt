@file:JvmName("CValues")
/*
 * some code is copied from `gregtech.api.GTValues`.
 */

package com.github.trc.clayium.api

// todo replace MOD_ID to this
const val MOD_ID = "clayium"
const val MOD_NAME = "Clayium"

const val GUI_DEFAULT_WIDTH: Int = 176
const val GUI_DEFAULT_HEIGHT: Int = 166

const val HARDNESS_UNBREAKABLE: Float = -1.0f

/**
 * This is worth exactly one normal Item.
 * This Constant can be divided by many commonly used Numbers such as
 * 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 24, ... 64 or 81
 * without losing precision and is for that reason used as Unit of Amount.
 * But it is also small enough to be multiplied with larger Numbers.
 *
 * This is used to determine the amount of Material contained inside a prefixed Ore.
 * For example, Nugget = `M / 9` as it contains out of 1/9 of an Ingot.
 *
 * Copied from `gregtech.api.GTValues`
 */
const val M: Long = 3628800L