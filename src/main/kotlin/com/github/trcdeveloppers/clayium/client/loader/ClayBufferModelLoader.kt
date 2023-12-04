package com.github.trcdeveloppers.clayium.client.loader

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.client.model.ClayBufferModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class ClayBufferModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        if (!(modelLocation.namespace == Clayium.MOD_ID && modelLocation is ModelResourceLocation)) {
            return false
        }
        val registryName = modelLocation.path
        println(registryName)
        return registryName.startsWith("buffer_tier")
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        val registryName = modelLocation.path
        return ClayBufferModel(registryName.last().digitToInt())
    }
}