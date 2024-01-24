package com.github.trcdevelopers.clayium.client.loader

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.client.model.ClayContainerModel
import com.github.trcdevelopers.clayium.client.model.ClayContainerPipeModel
import com.github.trcdevelopers.clayium.common.annotation.CBlock
import com.github.trcdevelopers.clayium.common.annotation.LoadWithCustomLoader
import com.google.common.reflect.ClassPath
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

internal fun ModelResourceLocation.getVariantValue(variantName: String): String? {
    if (!this.toString().contains(variantName)) return null
    return this.variant.replaceBefore("$variantName=", "")
        .replaceAfter(",", "")
        .replace("$variantName=", "").replace(",", "")
}

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
                    Clayium.LOGGER.warn("${cBlock.registryName} has no tier and Annotated with LoadWithCustomLoader. Ignored.")
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
        if (!(modelLocation.namespace == Clayium.MOD_ID && modelLocation is ModelResourceLocation)) {
            return false
        }
        if (modelLocation.variant == "normal") {
            return false
        }

        return modelLocation.path in this.customLoaderUsers.keys
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        val modelResourceLocation = modelLocation as ModelResourceLocation
        val registryName = modelResourceLocation.path
        val facing = EnumFacing.byName(modelResourceLocation.getVariantValue("facing")) ?: return ModelLoaderRegistry.getMissingModel()

        val modelData = this.customLoaderUsers[registryName] ?: return ModelLoaderRegistry.getMissingModel()

        return if (modelResourceLocation.getVariantValue("is_pipe")?.toBoolean() == true) {
            ClayContainerPipeModel(modelData.tier)
        } else {
            ClayContainerModel(
                modelData.tier,
                modelData.getFaceTexture() ?: ResourceLocation("builtin/missing"),
                facing,
            )
        }
    }

    class CModelData(
        val tier: Int,
        private val faceTextureName: String,
    ) {
        fun getFaceTexture(): ResourceLocation? {
            return if (this.faceTextureName.isEmpty()) null else ResourceLocation(Clayium.MOD_ID, "blocks/${this.faceTextureName}")
        }
    }
}