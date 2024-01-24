package com.github.trcdevelopers.clayium.common.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CBlock(
    val registryName: String,
    /**
     * tiers of the block.
     * The tier is appended to the registryName.
     * e.g. The block with registryName "clayium_block", and with tiers = [0, 1, 2]
     * will have registryNames "clayium_block_tier0", "clayium_block_tier1", "clayium_block_tier2".
     */
    val tiers: IntArray = [],
)
