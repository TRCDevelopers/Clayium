package com.github.trc.clayium.client.model

import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.common.Clayium
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry

object MetaTileEntityModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        // no-op
    }

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        if (!(modelLocation.namespace == CValues.MOD_ID
                    && modelLocation is ModelResourceLocation)) {
            return false
        }

        return modelLocation.path == "machine"
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        Clayium.LOGGER.info("Loading model for $modelLocation")
        if (modelLocation !is ModelResourceLocation) return ModelLoaderRegistry.getMissingModel()

        val isPipe = modelLocation.variant.split("=").last().toBooleanStrict()
        return MetaTileEntityModel(isPipe)
    }
}