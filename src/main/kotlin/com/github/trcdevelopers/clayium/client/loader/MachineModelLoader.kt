package com.github.trcdevelopers.clayium.client.loader

import com.github.trcdevelopers.clayium.client.model.machine.MachineModel
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel

object MachineModelLoader : MachineModelLoaderBase() {

    override val loadWithThis = setOf(
        MachineBlocks.Name.BUFFER,
    )

    override fun loadModel(modelLocation: ResourceLocation, machineName: String, tier: Int, isPipe: Boolean, properties: Map<String, String>): IModel {
        return MachineModel(isPipe, ResourceLocation("clayium:blocks/machinehull_tier$tier"),)
    }
}