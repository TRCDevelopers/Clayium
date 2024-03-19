package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasCompoundTag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.CombinedInvWrapper

/**
 * single input with single output
 *
 * todo: add recipe support, implement things
 */
class TileSimpleMachine : TileMachine() {

    override lateinit var autoIoHandler: AutoIoHandler

    private lateinit var inputItemHandler: ItemStackHandler
    private lateinit var outputItemHandler: ItemStackHandler
    private lateinit var combinedHandler: CombinedInvWrapper

    override fun openGui(player: EntityPlayer, world: World, pos: BlockPos) {
    }

    override fun getItemHandler() = combinedHandler

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        val inputSize = inputModes.size - 2 // - NONE, CE
        val outputSize = outputModes.size - 1 // - NONE
        inputItemHandler = ItemStackHandler(inputSize)
        outputItemHandler = ItemStackHandler(outputSize)
        combinedHandler = CombinedInvWrapper(inputItemHandler, outputItemHandler)
        autoIoHandler = AutoIoHandler(
            ConfigTierBalance.machineInterval[tier],
            ConfigTierBalance.machineAmount[tier],
        )
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("input_inventory", inputItemHandler.serializeNBT())
        compound.setTag("output_inventory", outputItemHandler.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasCompoundTag("input_inventory")) inputItemHandler.deserializeNBT(compound.getCompoundTag("input_inventory"))
        if (compound.hasCompoundTag("output_inventory")) outputItemHandler.deserializeNBT(compound.getCompoundTag("output_inventory"))
    }

    companion object {
        fun create(tier: Int): TileSimpleMachine {
            return TileSimpleMachine().apply {
                initParams(tier, MachineIoMode.Input.SINGLE, MachineIoMode.Output.SINGLE)
            }
        }
    }
}