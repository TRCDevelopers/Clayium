package com.github.trcdevelopers.clayium.client.loader

import com.github.trcdevelopers.clayium.client.model.machine.MachineModel
import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object MachineModelLoader : ICustomModelLoader {

    private val loadWithThis = setOf(
        "bending_machine",
    )

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        // no-op
    }

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
        val facing = EnumFacing.byName(properties["facing"])

        if (tier == null || isPipe == null || facing == null) return ModelLoaderRegistry.getMissingModel()

        val machineName = modelLocation.getName().replaceAfter("_tier", "").replace("_tier", "")

        return MachineModel(isPipe, facing,
            faceLocation = ResourceLocation(Clayium.MOD_ID, "blocks/$machineName"),
            machineHullLocation = ResourceLocation(Clayium.MOD_ID, "blocks/machinehull_tier$tier"),)
    }

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