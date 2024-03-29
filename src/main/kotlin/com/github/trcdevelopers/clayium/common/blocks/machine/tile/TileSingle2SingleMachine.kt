package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import com.github.trcdevelopers.clayium.common.recipe.CRecipes
import com.github.trcdevelopers.clayium.common.recipe.SimpleCeRecipe
import com.github.trcdevelopers.clayium.common.recipe.registry.SimpleCeRecipeRegistry
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasCompoundTag
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasInt
import com.github.trcdevelopers.clayium.common.util.NBTTypeUtils.hasString
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
 */
class TileSingle2SingleMachine : TileCeMachine() {

    var guiTranslationKey: String = ""
        private set

    override lateinit var autoIoHandler: AutoIoHandler

    private lateinit var inputItemHandler: ItemStackHandler
    private lateinit var outputItemHandler: ItemStackHandler
    private lateinit var combinedHandler: CombinedInvWrapper

    private lateinit var recipeRegistry: SimpleCeRecipeRegistry
    private var recipe: SimpleCeRecipe? = null
    private var canStartCraft = false

    var requiredProgress: Int = 0
    var craftingProgress: Int = 0

    private var recipeInitializedOnFirstTick = false

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
            inputItemHandler,
            outputItemHandler,
        )
    }

    override fun update() {
        super.update()
        if (world == null || world.isRemote) return
        if (!recipeInitializedOnFirstTick) {
            onInputSlotChanged()
            onOutputSlotChanged()
            recipeInitializedOnFirstTick = true
        }
        proceedCraft()
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("input_inventory", inputItemHandler.serializeNBT())
        compound.setTag("output_inventory", outputItemHandler.serializeNBT())
        compound.setString("recipe_registry", recipeRegistry.registryName)
        compound.setString("gui_machine_name", guiTranslationKey)
        compound.setInteger("crafting_progress", craftingProgress)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasCompoundTag("input_inventory")) inputItemHandler.deserializeNBT(compound.getCompoundTag("input_inventory"))
        if (compound.hasCompoundTag("output_inventory")) outputItemHandler.deserializeNBT(compound.getCompoundTag("output_inventory"))
        if (compound.hasString("recipe_registry")) recipeRegistry = CRecipes.getSimpleCeRecipeRegistry(compound.getString("recipe_registry")) ?: SimpleCeRecipeRegistry.EMPTY_1_1
        if (compound.hasString("gui_machine_name")) guiTranslationKey = compound.getString("gui_machine_name")
        if (compound.hasInt("crafting_progress")) craftingProgress = compound.getInteger("crafting_progress")
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return if (facing == null) true else acceptInputFrom(facing) || acceptOutputTo(facing)
        }
        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ITEM_HANDLER_CAPABILITY) {
            return if (facing == null || (canAutoInput(facing) && canAutoOutput(facing))) {
                combinedHandler as T
            } else if (_inputs[facing.index] == MachineIoMode.CE) {
                ceSlot as T
            }
            else if (canAutoInput(facing)) {
                inputItemHandler as T
            } else if (canAutoOutput(facing)) {
                outputItemHandler as T
            } else {
                null
            }
        }
        return super.getCapability(capability, facing)
    }

    private fun proceedCraft() {
        if (!canStartCraft) return

        val currentRecipe = recipe ?: return
        if (tryConsumeCe(currentRecipe.cePerTick)) {
            craftingProgress++
        }
        if (craftingProgress >= requiredProgress) {
            // craft finished. onInput/OutputSlotChanged will be called, so no need to reset params here
            val output = currentRecipe.getOutput(0)
            outputItemHandler.insertItem(0, output, false)
            inputItemHandler.extractItem(0, currentRecipe.inputs[0].amount, false)
        }
    }

    private fun onInputSlotChanged() {
        if (world.isRemote) return
        val inputStack = inputItemHandler.getStackInSlot(0)
        if (inputStack.isEmpty) {
            resetRecipe()
            return
        }

        recipe = recipeRegistry.getRecipe(inputStack)
        val recipeGot = recipe
        if (recipeGot == null) {
            resetRecipe()
        } else {
            canStartCraft = canOutputMerge(recipeGot.getOutput(0))
            requiredProgress = recipeGot.requiredTicks
            craftingProgress = 0
        }
    }

    private fun onOutputSlotChanged() {
        if (world.isRemote) return
        canStartCraft = recipe?.getOutput(0)?.let { canOutputMerge(it) } ?: false
    }

    private fun canOutputMerge(stack: ItemStack): Boolean {
        val outputSlot = outputItemHandler.getStackInSlot(0)
        if (outputSlot.isEmpty) return true

        return outputSlot.isItemEqual(stack)
                && (!stack.hasSubtypes || outputSlot.metadata == stack.metadata)
                && ItemStack.areItemStackTagsEqual(outputSlot, stack)
                && outputSlot.count + stack.count <= minOf(outputSlot.maxStackSize, outputItemHandler.getSlotLimit(0))
    }

    private fun resetRecipe() {
        recipe = null
        canStartCraft = false
        requiredProgress = 0
        craftingProgress = 0
    }

    override fun canAutoInput(side: EnumFacing): Boolean {
        return _inputs[side.index] == MachineIoMode.ALL
    }

    override fun canAutoOutput(side: EnumFacing): Boolean {
        return _outputs[side.index] == MachineIoMode.ALL
    }

    companion object {
        fun create(tier: Int, recipeRegistry: SimpleCeRecipeRegistry, guiTranslationKey: String): TileSingle2SingleMachine {
            return TileSingle2SingleMachine().apply {
                initParams(tier, MachineIoMode.Input.SINGLE, MachineIoMode.Output.SINGLE)
                this.recipeRegistry = recipeRegistry
                this.guiTranslationKey = guiTranslationKey
            }
        }
    }
}