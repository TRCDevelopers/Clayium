package com.github.trcdevelopers.clayium.client.loader

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel

class MachineModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        // no-op
    }

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        TODO("Not yet implemented")
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        TODO("Not yet implemented")
    }

    /**
     * Splits location into registryName and properties
     */
    private fun ModelResourceLocation.splitToNameAndProperties(): Pair<String, String> {
        return this.path.split("#").let { it[0] to it[1] }
    }
}