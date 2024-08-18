package com.github.trc.clayium.common.blocks.properties

import com.github.trc.clayium.api.unification.material.CMaterial
import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import net.minecraft.block.properties.PropertyHelper

class CMaterialProperty(
    allowedValues: Collection<CMaterial>,
    name: String,
) : PropertyHelper<CMaterial>(name, CMaterial::class.java) {

    private val allowedValues = ImmutableList.copyOf(allowedValues)

    override fun getAllowedValues(): ImmutableList<CMaterial> {
        return allowedValues
    }

    override fun parseValue(value: String): Optional<CMaterial> {
        val modIdAndName = value.split("__")
        if (modIdAndName.size != 2) return Optional.absent()
        val namespace = modIdAndName[0]
        val path = modIdAndName[1]
        val material = allowedValues.find { it.materialId.namespace == namespace && it.materialId.path == path }
        return Optional.fromNullable(material)
    }

    override fun getName(value: CMaterial): String {
        // we can't use ":" in blockState property names
        return "${value.materialId.namespace}__${value.materialId.path}"
    }
}