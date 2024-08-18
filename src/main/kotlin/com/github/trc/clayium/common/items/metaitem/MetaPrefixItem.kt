package com.github.trc.clayium.common.items.metaitem

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.items.metaitem.component.IItemColorHandler
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader

open class MetaPrefixItem private constructor(
    val name: String,
    val orePrefix: OrePrefix,
) : MetaItemClayium(name) {

    open fun registerSubItems() {
        for (material in ClayiumApi.materialRegistry) {
            if (!orePrefix.canGenerateItem(material)) continue
            val materialName = material.materialId.path

            addItem(material.metaItemSubId.toShort(), materialName) {
                tier(material.tier?.numeric ?: -1)
                oreDict(orePrefix, material)
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
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation("${material.materialId}_${orePrefix.snake}", "inventory"))
            } else {
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation(clayiumId("colored/${orePrefix.snake}"), "inventory"))
            }
        }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val material = getMaterial(stack) ?: return "invalid"
        return orePrefix.getLocalizedName(material)
    }

    private fun getMaterial(stack: ItemStack): CMaterial? {
        return getMaterial(stack.itemDamage)
    }

    private fun getMaterial(id: Int): CMaterial? {
        return ClayiumApi.materialRegistry.getObjectById(id)
    }

    companion object {
        fun create(name: String, orePrefix: OrePrefix): MetaPrefixItem {
            return when (orePrefix) {
                OrePrefix.impureDust -> MetaPrefixItemImpureDust
                OrePrefix.gem -> object : MetaPrefixItem(name, OrePrefix.gem) {
                    override fun registerModels() {
                        for (item in metaValueItems.values) {
                            ModelLoader.setCustomModelResourceLocation(
                                this, item.meta.toInt(),
                                ClayiumApi.materialRegistry.getObjectById(item.meta.toInt())?.getProperty(CPropertyKey.MATTER)?.modelLocation ?: ModelLoader.MODEL_MISSING
                            )
                        }
                    }
                }
                else -> MetaPrefixItem(name, orePrefix)
            }
        }
    }

    private object MetaPrefixItemImpureDust : MetaPrefixItem("meta_impure_dust", OrePrefix.impureDust) {
       override fun registerSubItems() {
            for (material in ClayiumApi.materialRegistry) {
                if (orePrefix.canGenerateItem(material)) {
                    val impureDust  = material.getProperty(CPropertyKey.IMPURE_DUST)
                    addItem(material.metaItemSubId.toShort(), material.materialId.path)
                        .tier(6)
                        .addComponent(IItemColorHandler { _, i -> impureDust.getColor(i) })
                        .oreDict(orePrefix, material)
                }
            }
        }

        override fun registerModels() {
            for (item in metaValueItems.values) {
                ModelLoader.setCustomModelResourceLocation(
                    this, item.meta.toInt(),
                    ModelResourceLocation(clayiumId("colored/dust"), "inventory")
                )
            }
        }
    }
}