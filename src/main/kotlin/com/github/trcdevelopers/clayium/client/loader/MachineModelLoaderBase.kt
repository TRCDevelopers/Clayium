package com.github.trcdevelopers.clayium.client.loader

import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry

abstract class MachineModelLoaderBase : ICustomModelLoader {

    protected abstract val loadWithThis: Set<String>

    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        if (!(modelLocation.namespace == Clayium.MOD_ID
                    && modelLocation is ModelResourceLocation)) {
            return false
        }
        // tier of a block model is in the registryName. (e.g. "clayium:bending_machine_tier1#facing=north")
        // tier of an item model is in the properties string (e.g. "clayium:bending_machine#tier=1")
        // item model is not handled by this loader.
        if ("tier=" in modelLocation.variant) return false
        return modelLocation.getName().replaceAfter("_tier", "").replace("_tier", "") in loadWithThis
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        if (modelLocation !is ModelResourceLocation) return ModelLoaderRegistry.getMissingModel()

        val properties = modelLocation.getProperties()
        val tier = modelLocation.getName().replaceBefore("tier", "").replace("tier", "").toIntOrNull()
        val isPipe = properties["is_pipe"]?.toBooleanStrictOrNull()

        if (tier == null || isPipe == null) return ModelLoaderRegistry.getMissingModel()

        val machineName = modelLocation.getName().replaceAfter("_tier", "").replace("_tier", "")

        return loadModel(modelLocation, machineName, tier, isPipe, properties)
    }

    protected abstract fun loadModel(modelLocation: ResourceLocation, machineName: String, tier: Int, isPipe: Boolean, properties: Map<String, String>): IModel

    private fun ModelResourceLocation.getName(): String {
        return this.path.split("#").first()
    }

    private fun ModelResourceLocation.getProperties(): Map<String, String> {
        // properties in raw string ("facing=north,is_pipe=false")
        return this.variant
            // (["facing=north", "is_pipe=false"])
            .split(",")
            .associate {
                val kvPair = it.split("=")
                kvPair.first() to kvPair.last()
            }
    }
}