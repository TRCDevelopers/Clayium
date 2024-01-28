package com.github.trcdevelopers.clayium.common.items

interface IMaterial {
    val materialName: String

    /**
     * The suffix used for the ore dictionary name of this material.
     * dust${oreDictSuffix}, ingot${oreDictSuffix}, etc.
     */
    val oreDictSuffix: String

    /**
     * -1 if none.
     */
    val tier: Int

    /**
     * Used with tintIndex in the Model file.
     * Make sure that the array index and tintIndex correspond.
     * 0xFFFFFF if the material is not colored.
     */
    val colors: IntArray
}