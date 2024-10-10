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
        if (modelLocation !is ModelResourceLocation) return false
        return modelLocation.namespace == MOD_ID
                && modelLocation.path == "material/compressed_material"
                && modelLocation.variant == "variant=block"

    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        return MetalBlockModel()
    }
}