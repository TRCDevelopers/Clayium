package com.github.trc.clayium.common.items.metaitem

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.common.Clayium
import com.github.trc.clayium.common.items.metaitem.component.IItemColorHandler
import com.github.trc.clayium.common.unification.material.Material
import com.github.trc.clayium.common.unification.material.PropertyKey
import com.github.trc.clayium.common.unification.ore.OrePrefix
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
                ModelLoader.setCustomModelResourceLocation(this, item.meta.toInt(), ModelResourceLocation("${Clayium.MOD_ID}:colored/${orePrefix.snake}", "inventory"))
            }
        }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val material = getMaterial(stack) ?: return "invalid"
        return orePrefix.getLocalizedName(material)
    }

    private fun getMaterial(stack: ItemStack): Material? {
        return getMaterial(stack.itemDamage)
    }

    private fun getMaterial(id: Int): Material? {
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
                                ClayiumApi.materialRegistry.getObjectById(item.meta.toInt())?.getProperty(PropertyKey.MATTER)?.modelLocation ?: ModelLoader.MODEL_MISSING
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
                    val impureDust  = material.getProperty(PropertyKey.IMPURE_DUST)
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
                    ModelResourceLocation("${Clayium.MOD_ID}:colored/dust", "inventory")
                )
            }
        }
    }
}