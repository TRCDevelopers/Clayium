package com.github.trcdevelopers.clayium.common.unification.material

data class PropertyKey<T : MaterialProperty>(
    val name: String,
)  {
    @Suppress("UNCHECKED_CAST")
    fun cast(value: MaterialProperty): T {
        return value as T
    }

    companion object {
        val INGOT = PropertyKey<MaterialProperty.Ingot>("ingot")
        val DUST = PropertyKey<MaterialProperty.Dust>("dust")
        val MATTER = PropertyKey<MaterialProperty.Matter>("matter")
        val IMPURE_DUST = PropertyKey<MaterialProperty.ImpureDust>("impureDust")
        val PLATE = PropertyKey<MaterialProperty.Plate>("plate")
        val CLAY = PropertyKey<Clay>("clay")
        val BLAST_SMELTING = PropertyKey<BlastSmelting>("blastSmelting")
    }
}