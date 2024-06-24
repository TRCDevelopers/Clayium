package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemColorHandler
import com.github.trcdevelopers.clayium.common.unification.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial
import com.github.trcdevelopers.clayium.common.unification.material.MaterialProperty
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader

open class MetaPrefixItem private constructor(
    val name: String,
    val orePrefix: OrePrefix,
) : MetaItemClayium(name) {

    open fun registerSubItems() {
        for (material in EnumMaterial.entries) {
            if (!orePrefix.doGenerateItem(material)) continue

            addItem(material.uniqueId.toShort(), material.materialName) {
                tier(material.tier)
                oreDict("${orePrefix.camel}${CUtils.toUpperCamel(material.materialName)}")
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
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation("${Clayium.MOD_ID}:${material.materialName}_${orePrefix.snake}", "inventory"))
            } else {
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation("${Clayium.MOD_ID}:colored/${orePrefix.snake}", "inventory"))
            }
        }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val material = getMaterial(stack) ?: return "invalid"
        return I18n.format("oreprefix.${Clayium.MOD_ID}.${orePrefix.camel}", I18n.format("material.${Clayium.MOD_ID}.${material.materialName}"))
    }

    private fun getMaterial(stack: ItemStack): EnumMaterial? {
        return EnumMaterial.fromId(stack.itemDamage)
    }

    private fun getMaterial(id: Int): EnumMaterial? {
        return EnumMaterial.fromId(id)
    }

    companion object {


        fun create(name: String, orePrefix: OrePrefix): MetaPrefixItem {
            return when (orePrefix) {
                OrePrefix.IMPURE_DUST -> MetaPrefixItemImpureDust
                OrePrefix.MATTER -> object : MetaPrefixItem(name, OrePrefix.MATTER) {
                    override fun registerModels() {
                        for (item in metaValueItems.values) {
                            ModelLoader.setCustomModelResourceLocation(
                                this, item.meta.toInt(),
                                EnumMaterial.fromId(item.meta.toInt())?.getProperty<MaterialProperty.Matter>()?.modelLocation ?: ModelLoader.MODEL_MISSING
                            )
                        }
                    }
                }
                else -> MetaPrefixItem(name, orePrefix)
            }
        }
    }

    private object MetaPrefixItemImpureDust : MetaPrefixItem("meta_impure_dust", OrePrefix.IMPURE_DUST) {
       override fun registerSubItems() {
            for (material in EnumMaterial.entries) {
                if (OrePrefix.IMPURE_DUST.doGenerateItem(material)) {
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