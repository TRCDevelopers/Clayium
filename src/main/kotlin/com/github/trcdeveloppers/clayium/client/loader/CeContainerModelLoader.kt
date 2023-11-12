package com.github.trcdeveloppers.clayium.client.loader

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.client.model.CeContainerBakedModel
import com.github.trcdeveloppers.clayium.client.model.CeContainerModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import java.util.function.Function

class CeContainerModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        val modelLocStr = modelLocation.toString()
        if (modelLocation.namespace != Clayium.MOD_ID
            || modelLocStr.contains("#inventory")
            || modelLocStr.contains("item")) return false

        if (modelLocStr.contains("test_single_slot_machine")){
            return true
        }

        return false
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        return CeContainerModel()
    }
}