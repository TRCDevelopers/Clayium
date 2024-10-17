package com.github.trc.clayium.api.extensions.ccl

import codechicken.lib.colour.ColourARGB
import codechicken.lib.colour.ColourRGBA

/** @return `[a, r, g, b]` */
fun ColourARGB.packIntArray(): IntArray {
    return intArrayOf(
        a.toInt() and 0xFF,
        r.toInt() and 0xFF,
        g.toInt() and 0xFF,
        b.toInt() and 0xFF
    )
}

/** @return `[r, g, b, a]` */
fun ColourRGBA.packIntArray(): IntArray {
    return intArrayOf(
        r.toInt() and 0xFF,
        g.toInt() and 0xFF,
        b.toInt() and 0xFF,
        a.toInt() and 0xFF
    )
}
