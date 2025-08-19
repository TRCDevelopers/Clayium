package io.github.trcdevelopers.clayium.client.model

import io.github.trcdevelopers.clayium.api.MOD_ID
import io.github.trcdevelopers.clayium.api.util.clayiumId
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelDynBucket

object FluidCapsuleModelLoader : ICustomModelLoader {

    private val MODEL_1mb = ModelDynBucket(null, clayiumId("items/capsule0001_mask"), clayiumId("items/capsule0001"), null, false, true)
    private val MODEL_5mb = ModelDynBucket(null, clayiumId("items/capsule0005_mask"), clayiumId("items/capsule0005"), null, false, true)
    private val MODEL_25mb = ModelDynBucket(null, clayiumId("items/capsule0025_mask"), clayiumId("items/capsule0025"), null, false, true)
    private val MODEL_125mb = ModelDynBucket(null, clayiumId("items/capsule0125_mask"), clayiumId("items/capsule0125"), null, false, true)
    private val MODEL_1000mb = ModelDynBucket(null, clayiumId("items/capsule1000_mask"), clayiumId("items/capsule1000"), null, false, true)

    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun accepts(modelLocation: ResourceLocation): Boolean {

        return modelLocation is ModelResourceLocation
                && modelLocation.namespace == MOD_ID
                && modelLocation.path.startsWith("fluid_capsule_")
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        val loc = modelLocation as ModelResourceLocation
        val capacity = loc.path.removePrefix("fluid_capsule_").toIntOrNull() ?: return MODEL_1mb
        return when (capacity) {
            1 -> MODEL_1mb
            5 -> MODEL_5mb
            25 -> MODEL_25mb
            125 -> MODEL_125mb
            1000 -> MODEL_1000mb
            else -> MODEL_1mb
        }
    }
}