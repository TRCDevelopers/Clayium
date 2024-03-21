package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import com.github.trcdevelopers.clayium.common.recipe.CRecipes
import com.github.trcdevelopers.clayium.common.recipe.SimpleCeRecipe
import com.github.trcdevelopers.clayium.common.recipe.registry.SimpleCeRecipeRegistry
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasCompoundTag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.CombinedInvWrapper

/**
 * single input with single output
 *
 * todo: add recipe support, implement things
 */
class TileSingle2SingleMachine : TileCeMachine() {

    override lateinit var autoIoHandler: AutoIoHandler

    private lateinit var inputItemHandler: ItemStackHandler
    private lateinit var outputItemHandler: ItemStackHandler
    private lateinit var combinedHandler: CombinedInvWrapper

    private lateinit var recipeRegistry: SimpleCeRecipeRegistry
    private var recipe: SimpleCeRecipe? = null
    private var crafting = false

    var requiredProgress: Int = 0
    var craftingProgress: Int = 0

    override fun getItemHandler() = combinedHandler

    override fun openGui(player: EntityPlayer, world: World, pos: BlockPos) {
        player.openGui(Clayium, GuiHandler.SINGLE_2_SINGLE, world, pos.x, pos.y, pos.z)
    }

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        inputItemHandler = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                markDirty()
                onInputSlotChanged()
            }
        }
        outputItemHandler = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                markDirty()
                onOutputSlotChanged()
            }

            override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                return false
            }
        }
        combinedHandler = CombinedInvWrapper(inputItemHandler, outputItemHandler)
        autoIoHandler = AutoIoHandler(
            ConfigTierBalance.machineInterval[tier],
            ConfigTierBalance.machineAmount[tier],
        )
    }

    override fun update() {
        super.update()
        if (world.isRemote) return
        if (crafting) {
            val currentRecipe = recipe ?: return
            if (craftingProgress >= requiredProgress) {
                craftingProgress = 0
                requiredProgress = 0
                recipe = null
                crafting = false
                val output = currentRecipe.getOutput(0)
                outputItemHandler.insertItem(0, output, false)
                inputItemHandler.extractItem(0, currentRecipe.inputs[0].amount, false)
            } else {
                craftingProgress++
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("input_inventory", inputItemHandler.serializeNBT())
        compound.setTag("output_inventory", outputItemHandler.serializeNBT())
        compound.setString("recipe_registry", recipeRegistry.registryName)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasCompoundTag("input_inventory")) inputItemHandler.deserializeNBT(compound.getCompoundTag("input_inventory"))
        if (compound.hasCompoundTag("output_inventory")) outputItemHandler.deserializeNBT(compound.getCompoundTag("output_inventory"))
        if (compound.hasKey("recipe_registry")) recipeRegistry = CRecipes.getSimpleCeRecipeRegistry(compound.getString("recipe_registry")) ?: SimpleCeRecipeRegistry.EMPTY_1_1
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return if (facing == null) true else isImporting(facing) || isExporting(facing)
        }
        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return if (facing == null || (isImporting(facing) && isExporting(facing))) {
                ITEM_HANDLER_CAPABILITY.cast(combinedHandler)
            } else {
                if (isImporting(facing)) {
                    ITEM_HANDLER_CAPABILITY.cast(inputItemHandler)
                } else if (isExporting(facing)) {
                    ITEM_HANDLER_CAPABILITY.cast(outputItemHandler)
                } else {
                    null
                }
            }
        }
        return super.getCapability(capability, facing)
    }

    private fun onInputSlotChanged() {
        if (world.isRemote) return
        val inputStack = inputItemHandler.getStackInSlot(0)
        if (inputStack.isEmpty) {
            recipe = null
            crafting = false
            requiredProgress = 0
            craftingProgress = 0
            return
        }
        recipe = recipeRegistry.getRecipe(inputStack)
        crafting = recipe?.getOutput(0)?.let { canOutputMerge(it) } ?: false

        val recipeGot = recipe
        if (recipeGot != null && crafting) {
            requiredProgress = recipeGot.requiredTicks
            craftingProgress = 0
        }
    }

    private fun onOutputSlotChanged() {
        if (world.isRemote) return
        crafting = recipe?.getOutput(0)?.let { canOutputMerge(it) } ?: false
    }

    private fun canOutputMerge(stack: ItemStack): Boolean {
        val outputSlot = outputItemHandler.getStackInSlot(0)
        if (outputSlot.isEmpty) return true

        return outputSlot.isItemEqual(stack)
                && (!stack.hasSubtypes || outputSlot.metadata == stack.metadata)
                && ItemStack.areItemStackTagsEqual(outputSlot, stack)
                && outputSlot.count + stack.count <= minOf(outputSlot.maxStackSize, outputItemHandler.getSlotLimit(0))
    }

    companion object {
        fun create(tier: Int, recipeRegistry: SimpleCeRecipeRegistry): TileSingle2SingleMachine {
            return TileSingle2SingleMachine().apply {
                initParams(tier, MachineIoMode.Input.SINGLE, MachineIoMode.Output.SINGLE)
                this.recipeRegistry = recipeRegistry
            }
        }
    }
}