package com.github.trc.clayium.client.model

import com.github.trc.clayium.api.MOD_ID
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry

object MetaTileEntityModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        ModelTextures.isInitialized = false
    }

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        if (!(modelLocation.namespace == MOD_ID && modelLocation is ModelResourceLocation)) {
            return false
        }

        return modelLocation.path == "machine"
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        if (modelLocation !is ModelResourceLocation) return ModelLoaderRegistry.getMissingModel()

        val isPipe = modelLocation.variant.split("=").last().toBooleanStrict()
        return MetaTileEntityModel(isPipe)
    }
}
