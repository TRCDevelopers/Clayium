package com.github.trcdeveloppers.clayium.client.loader

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.client.ClayiumClientProxy
import com.github.trcdeveloppers.clayium.client.model.CeContainerModel
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel

class CeContainerModelLoader : ICustomModelLoader {
    override fun onResourceManagerReload(resourceManager: IResourceManager) {}

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        val modelLocStr = modelLocation.toString()
        if (modelLocation.namespace != Clayium.MOD_ID
            || !modelLocStr.contains("#")
            || modelLocStr.contains("#inventory")
            || modelLocStr.contains("item")) return false

        val registryName = modelLocStr.replaceAfter("#", "").replace("#", "")
        // Special cases such as ClayReactor
        if (modelLocStr.contains("test_single_slot_machine")) {
            Clayium.LOGGER.info("CeContainerModelLoader: accepts: true")
            return true
        }

        if (registryName in proxy.customLoaderUsers) {
            val tier = registryName.replaceBefore("tier", "").toIntOrNull() ?: run {
                Clayium.LOGGER.error("Detected tiered block, but could not extract tier from it.")
                return false
            }
            Clayium.LOGGER.info("REG: $registryName, $tier")
        }

        return false
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {
        return CeContainerModel()
    }

    companion object {
        val proxy = Clayium.proxy as ClayiumClientProxy
    }
}