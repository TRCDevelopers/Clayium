package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemColorHandler
import com.github.trcdevelopers.clayium.common.unification.EnumOrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader

open class MetaPrefixItem private constructor(
    val name: String,
    val enumOrePrefix: EnumOrePrefix,
) : MetaItemClayium(name) {

    open fun registerSubItems() {
        for (material in EnumMaterial.entries) {
            if (!enumOrePrefix.doGenerateItem(material)) continue

            addItem(material.uniqueId.toShort(), material.materialName) {
                tier(material.tier)
                oreDict("${enumOrePrefix.camel}${CUtils.toUpperCamel(material.materialName)}")
                if (material.colors != null) {
                    addComponent(IItemColorHandler { _, i -> material.colors[i] })
                }
            }
        }
    }

    override fun registerModels() {
        for (item in metaValueItems.values) {
            val material = getMaterial(item.meta.toInt()) ?: continue
            if (material.colors == null) {
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation("${Clayium.MOD_ID}:${material.materialName}_${enumOrePrefix.snake}", "inventory"))
            } else {
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation("${Clayium.MOD_ID}:colored/${enumOrePrefix.snake}", "inventory"))
            }
        }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val material = getMaterial(stack) ?: return "invalid"
        return I18n.format("oreprefix.${Clayium.MOD_ID}.${enumOrePrefix.camel}", I18n.format("material.${Clayium.MOD_ID}.${material.materialName}"))
    }

    private fun getMaterial(stack: ItemStack): EnumMaterial? {
        return EnumMaterial.fromId(stack.itemDamage)
    }

    private fun getMaterial(id: Int): EnumMaterial? {
        return EnumMaterial.fromId(id)
    }

    companion object {


        fun create(name: String, enumOrePrefix: EnumOrePrefix): MetaPrefixItem {
            return when (enumOrePrefix) {
                EnumOrePrefix.IMPURE_DUST -> MetaPrefixItemImpureDust
                EnumOrePrefix.MATTER -> object : MetaPrefixItem(name, EnumOrePrefix.MATTER) {
                    override fun registerModels() {
                        for (item in metaValueItems.values) {
                            ModelLoader.setCustomModelResourceLocation(
                                this, item.meta.toInt(),
                                EnumMaterial.fromId(item.meta.toInt())?.getProperty<MaterialProperty.Matter>()?.modelLocation ?: ModelLoader.MODEL_MISSING
                            )
                        }
                    }
                }
                else -> MetaPrefixItem(name, enumOrePrefix)
            }
        }
    }

    private object MetaPrefixItemImpureDust : MetaPrefixItem("meta_impure_dust", EnumOrePrefix.IMPURE_DUST) {
       override fun registerSubItems() {
            for (material in EnumMaterial.entries) {
                if (EnumOrePrefix.IMPURE_DUST.doGenerateItem(material)) {
                    val impureDust  = material.getProperty<MaterialProperty.ImpureDust>()!!
                    addItem(material.uniqueId.toShort(), material.materialName)
                        .tier(material.tier)
                        .addComponent(IItemColorHandler { _, i -> impureDust.getColor(i) })
                        .oreDict("impureDust${CUtils.toUpperCamel(material.materialName)}")
                }
            }
        }

        override fun registerModels() {
            for (item in metaValueItems.values) {
                ModelLoader.setCustomModelResourceLocation(
                    this, item.meta.toInt(),
                    ModelResourceLocation("${Clayium.MOD_ID}:colored/dust", "inventory")
                )
            }
        }
    }
}