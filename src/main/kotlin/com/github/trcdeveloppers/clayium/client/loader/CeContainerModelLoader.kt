package com.github.trcdeveloppers.clayium.client.loader

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.client.model.CeContainerModel
import com.github.trcdeveloppers.clayium.common.annotation.CBlock
import com.github.trcdeveloppers.clayium.common.annotation.LoadWithCustomLoader
import com.google.common.reflect.ClassPath
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class CeContainerModelLoader : ICustomModelLoader {

    private val customLoaderUsers = mutableMapOf<String, CModelData>()

    init {
        val classLoader = Thread.currentThread().contextClassLoader
        ClassPath.from(classLoader).getTopLevelClassesRecursive("com.github.trcdeveloppers.clayium.common.blocks")
            .map(ClassPath.ClassInfo::load)
            .forEach { clazz ->
                val loadWithCustomLoader = clazz.getAnnotation(LoadWithCustomLoader::class.java) ?: return@forEach
                val faceTexture = loadWithCustomLoader.faceTexture
                val cBlock = clazz.getAnnotation(CBlock::class.java) ?: return@forEach
                if (cBlock.tiers.isEmpty() ) {
                    customLoaderUsers[cBlock.registryName] = CModelData.EMPTY
                    return@forEach
                } else if (cBlock.tiers.size == 1) {
                    customLoaderUsers[cBlock.registryName] = CModelData(cBlock.tiers[0], faceTexture)
                    return@forEach
                }

                cBlock.tiers.forEach { tier ->
                    customLoaderUsers["${cBlock.registryName}_tier$tier"] = CModelData(tier, faceTexture)
                }
            }
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        val modelLocStr = modelLocation.toString()
        if (modelLocation.namespace != Clayium.MOD_ID
            || !modelLocStr.contains("#")
            || modelLocStr.contains("#inventory")
            || modelLocStr.contains("item")) return false

        val registryName = modelLocation.toString().replaceAfter("#", "").replace("#", "")
            .replace("${Clayium.MOD_ID}:", "")

        return registryName in this.customLoaderUsers.keys
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        val registryName = modelLocation.toString().replaceAfter("#", "").replace("#", "")
            .replace("${Clayium.MOD_ID}:", "")
       val facing = EnumFacing.byName(
           modelLocation.toString().replaceBefore("facing=", "")
               .replaceAfter(",", "")
               .replace("facing=", "").replace(",", "")
       ) ?: return ModelLoaderRegistry.getMissingModel()

        val modelData = this.customLoaderUsers[registryName] ?: return ModelLoaderRegistry.getMissingModel()
        return CeContainerModel(
            modelData.tier,
            modelData.getFaceTexture() ?: ResourceLocation("builtin/missing"),
            facing,
        )
    }

    data class CModelData(
        val tier: Int,
        private val faceTextureName: String,
    ) {
        fun getFaceTexture(): ResourceLocation? {
            return if (this.faceTextureName.isEmpty()) {
                null
            } else {
                ResourceLocation(Clayium.MOD_ID, "blocks/${this.faceTextureName}")
            }
        }

        fun isEmpty(): Boolean {
            return this == EMPTY
        }
        companion object {
            val EMPTY = CModelData(0, "")
        }
    }
}