package com.github.trcdevelopers.clayium.common.blocks.machine.tile

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.github.trcdevelopers.clayium.common.ClayConstants
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.GuiHandler
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.config.ConfigTierBalance
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
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
import org.lwjgl.input.Keyboard

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
        player.openGui(Clayium.INSTANCE, GuiHandler.SINGLE_2_SINGLE, world, pos.x, pos.y, pos.z)
    }

    override fun initParams(tier: Int, inputModes: List<MachineIoMode>, outputModes: List<MachineIoMode>) {
        super.initParams(tier, inputModes, outputModes)
        inputItemHandler = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                onInputSlotChanged()
                markDirty()
            }
        }
        outputItemHandler = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                onOutputSlotChanged()
                markDirty()
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

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.syncValue("requiredProgress", 0, SyncHandlers.intNumber(
            { requiredProgress },
            { rProgress -> requiredProgress = rProgress }
        ))
        syncManager.syncValue("craftingProgress", 1, SyncHandlers.intNumber(
            { craftingProgress },
            { cProgress -> craftingProgress = cProgress }
        ))
        syncManager.syncValue("clayEnergy", 2, SyncHandlers.longNumber(
            { storedCe.energy },
            { storedCe = ClayEnergy(it) }
        ))

        return ModularPanel("single_to_single_machine")
            .flex {
                it.align(Alignment.Center)
            }
            .child(IKey.lang(guiTranslationKey, IKey.lang("${ClayConstants.MACHINE_TIER_LANG_KEY}$tier")).asWidget()
                .top(6)
                .left(6))
            .child(Row()
                .widthRel(0.6f).height(26)
                .align(Alignment.Center)
                .top(30)
                .child(Widget()
                    .size(26, 26)
                    .background(ClayGuiTextures.LARGE_SLOT)
                    .align(Alignment.CenterLeft))
                .child(ItemSlot().left(4).top(4)
                    .slot(SyncHandlers.itemSlot(inputItemHandler, 0)
                        .changeListener { _, _, client, init -> if (!(client || init)) onInputSlotChanged() }
                        .singletonSlotGroup(2))
                    .background(IDrawable.EMPTY))
                .child(ProgressWidget()
                    .size(22, 17)
                    .align(Alignment.Center)
                    .progress { this.craftingProgress.toDouble() / this.requiredProgress.toDouble() }
                    .texture(ClayGuiTextures.PROGRESS_BAR, 22))
                .child(Widget()
                    .size(26, 26)
                    .background(ClayGuiTextures.LARGE_SLOT)
                    .align(Alignment.CenterRight))
                .child(ItemSlot().right(4).top(4)
                    .slot(SyncHandlers.itemSlot(outputItemHandler, 0)
                        .changeListener { _, _, client, init -> if (!(client || init)) onOutputSlotChanged() }
                        .singletonSlotGroup(1))
                    .background(IDrawable.EMPTY)))
            .child(ItemSlot()
                .right(7).top(58)
                .setEnabledIf {
                    // is shift key down
                    Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)
                }
                .slot(object : ModularSlot(ceSlot, 0) {
                    override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
                        return false
                    }
                })
                .background(IDrawable.EMPTY))
            .child(IKey.dynamic { IKey.lang(ClayConstants.CE_LANG_KEY, storedCe.toString()).toString() }.asWidget()
                .widthRel(0.5f)
                .pos(6, 60))
            .bindPlayerInventory()
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
        Clayium.LOGGER.info("onInputSlotChanged")
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
        Clayium.LOGGER.info("canStartCraft In: $canStartCraft")
    }

    private fun onOutputSlotChanged() {
        Clayium.LOGGER.info("onOutputSlotChanged")
        if (world.isRemote) return
        canStartCraft = recipe?.getOutput(0)?.let { canOutputMerge(it) } ?: false
        Clayium.LOGGER.info("canStartCraft Out: $canStartCraft")
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