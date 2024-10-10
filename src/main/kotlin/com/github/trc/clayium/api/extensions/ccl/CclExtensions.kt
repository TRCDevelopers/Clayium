package com.github.trc.clayium.api.extensions.ccl

import codechicken.lib.colour.ColourARGB
import codechicken.lib.colour.ColourRGBA

/**
 * @return `[a, r, g, b]`
 */
fun ColourARGB.packIntArray(): IntArray {
    return intArrayOf(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

/**
 * @return `[r, g, b, a]`
 */
fun ColourRGBA.packIntArray(): IntArray {
    return intArrayOf(r.toInt(), g.toInt(), b.toInt(), a.toInt())
}
