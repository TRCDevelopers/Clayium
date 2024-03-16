package com.github.trcdevelopers.clayium.client.loader

import com.github.trcdevelopers.clayium.client.model.facedmachine.FacedMachineModel
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineBlocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object FacedMachineModelLoader : MachineModelLoaderBase() {

    override val loadWithThis = setOf(
        MachineBlocks.Name.BENDING,
    )

    override fun loadModel(modelLocation: ResourceLocation, machineName: String, tier: Int, isPipe: Boolean, properties: Map<String, String>): IModel {
        val facing = EnumFacing.byName(properties["facing"]) ?: EnumFacing.NORTH

        return FacedMachineModel(isPipe, facing,
            faceLocation = ResourceLocation(Clayium.MOD_ID, "blocks/$machineName"),
            machineHullLocation = ResourceLocation(Clayium.MOD_ID, "blocks/machinehull_tier$tier"),)
    }
}