package com.github.trc.clayium.api.unification.material

object CMaterialFlags {
    val GENERATE_CLAY_PARTS = CMaterialFlag("generate_clay_parts")

    /**
     * generates compressed block for the material.
     * also generates a recipe for compression and decompression.
     */
    val COMPRESSED_BLOCK = CMaterialFlag("compressed_block")
}