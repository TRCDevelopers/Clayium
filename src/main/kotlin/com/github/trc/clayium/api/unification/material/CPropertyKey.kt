package com.github.trc.clayium.api.unification.material

data class CPropertyKey<T : MaterialProperty>(
    val name: String,
) {
    @Suppress("UNCHECKED_CAST")
    fun cast(value: MaterialProperty): T {
        return value as T
    }

    companion object {
        val INGOT = CPropertyKey<MaterialProperty.Ingot>("ingot")
        val DUST = CPropertyKey<MaterialProperty.Dust>("dust")
        val MATTER = CPropertyKey<MaterialProperty.Matter>("matter")
        val IMPURE_DUST = CPropertyKey<MaterialProperty.ImpureDust>("impureDust")
        val PLATE = CPropertyKey<MaterialProperty.Plate>("plate")
        val CLAY = CPropertyKey<Clay>("clay")
        val CLAY_SMELTING = CPropertyKey<ClaySmelting>("claySmelting")
        val BLAST_SMELTING = CPropertyKey<BlastSmelting>("blastSmelting")
    }
}
