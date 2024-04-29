package com.github.trcdevelopers.clayium.client.model

import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.common.metatileentity.MetaTileEntities
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

        // tier of a block model is not in the location. (e.g. "clayium:bending_machine#is_pipe=false")
        // tier of an item model is in the properties string (e.g. "clayium:bending_machine#tier=1")
        // this loader does not handle an item model.
        if ("tier=" in modelLocation.variant) return false
        val name = modelLocation.path.split("#").first()
        return name in MetaTileEntities.names
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        if (modelLocation !is ModelResourceLocation) return ModelLoaderRegistry.getMissingModel()

        val propString = modelLocation.path.split("#").last()
        val isPipe = propString.split("=").last().toBooleanStrict()
        return MetaTileEntityModel(isPipe)
    }
}