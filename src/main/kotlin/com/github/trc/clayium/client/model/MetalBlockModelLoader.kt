package com.github.trc.clayium.client.model

import com.github.trc.clayium.api.MOD_ID
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel

object MetalBlockModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {
    }

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        return getModel(modelLocation) != null
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        return getModel(modelLocation) ?: throw Exception("MetalBlockModelLoader tried to load model $modelLocation, but it does not accept that location.")
    }

    private fun getModel(modelLocation: ResourceLocation): IModel? {
        if (modelLocation !is ModelResourceLocation) return null
        if (modelLocation.namespace != MOD_ID) return null

        return if (modelLocation.path.contains("metal_chest")) {
            MetalChestModel()
        } else if (modelLocation.path.contains("compressed_material") && modelLocation.variant == "variant=block") {
            MetalBlockModel()
        } else {
            null // Unsupported model location
        }
    }
}