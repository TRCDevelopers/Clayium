package io.github.trcdevelopers.clayium.common.blocks.clayworktable

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.api.widget.IGuiAction
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ProgressWidget
import com.cleanroommc.modularui.widgets.layout.Flow
import com.cleanroommc.modularui.widgets.slot.SlotGroup
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.gui.ButtonToggleable
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.CWTRecipes
import io.github.trcdevelopers.clayium.common.recipe.ClayWorkTableRecipe
import io.github.trcdevelopers.clayium.integration.jei.JeiPlugin
import io.github.trcdevelopers.clayium.integration.jei.clayworktable.ClayWorkTableRecipeCategory
import io.github.trcdevelopers.clayium.integration.modularui.IGuiHolderClayium
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class TileClayWorkTable : TileEntity(), IGuiHolderClayium<PosGuiData> {

    private val itemHandler = ItemStackHandler(4)
    var craftingProgress = 0
    var requiredProgress = 0
    private var currentRecipe: ClayWorkTableRecipe? = null

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("Inventory", itemHandler.serializeNBT())
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        itemHandler.deserializeNBT(compound.getCompoundTag("Inventory"))
        super.readFromNBT(compound)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === ITEM_HANDLER_CAPABILITY) capability.cast(itemHandler) else super.getCapability(capability, facing)
    }

    private val currentTool: ItemStack
        get() = itemHandler.getStackInSlot(1)

    @SideOnly(Side.CLIENT)
    fun getCraftingProgressScaled(scale: Int): Int {
        return if (requiredProgress == 0) 0 else craftingProgress * scale / requiredProgress
    }

    fun getNormalizedProgress(): Double {
        return if (requiredProgress == 0) 0.0 else craftingProgress.toDouble() / requiredProgress.toDouble()
    }

    fun canPushButton(id: Int): Boolean {
        return canStartCraft(itemHandler.getStackInSlot(0), ClayWorkTableMethod.fromId(id) ?: return false)
    }

    fun pushButton(clicker: EntityPlayer, id: Int) {
        val input = itemHandler.getStackInSlot(0)
        val method: ClayWorkTableMethod = ClayWorkTableMethod.fromId(id)
            ?: return
        val recipe = CWTRecipes.getClayWorkTableRecipe(input, method)
            ?: throw NullPointerException("Button pushed without any valid recipe! This should not happen. Please report this at the issue tracker.")
        if (currentRecipe !== recipe) {
            currentRecipe = recipe
            requiredProgress = recipe.clicks
            craftingProgress = 0
        }
        craftingProgress++
        recipe.method.damageTool(currentTool, clicker)
        if (craftingProgress >= requiredProgress) {
            itemHandler.extractItem(0, recipe.input.amount, false)
            completeRecipe(recipe)
        }
    }

    fun completeRecipe(recipe: ClayWorkTableRecipe) {
        itemHandler.insertItem(2, recipe.primaryOutput, false)
        itemHandler.insertItem(3, recipe.secondaryOutput, false)
        resetRecipe()
    }

    fun canStartCraft(input: ItemStack, method: ClayWorkTableMethod): Boolean {
        val recipe = CWTRecipes.getClayWorkTableRecipe(input, method) ?: return false
        if (!method.isValidTool(currentTool)) return false

        val outputSlot = itemHandler.getStackInSlot(2)
        val secondaryOutputSlot = itemHandler.getStackInSlot(3)

        val canOutputPrimary = (outputSlot.isEmpty || (outputSlot.isItemEqual(recipe.primaryOutput) && outputSlot.count + recipe.primaryOutput.count <= outputSlot.maxStackSize))
        val canOutputSecondary = (secondaryOutputSlot.isEmpty || (secondaryOutputSlot.isItemEqual(recipe.secondaryOutput) && secondaryOutputSlot.count + recipe.secondaryOutput.count <= secondaryOutputSlot.maxStackSize))

        return canOutputPrimary && (!recipe.hasSecondaryOutput() || canOutputSecondary)
    }

    private fun resetRecipe() {
        currentRecipe = null
        requiredProgress = 0
        craftingProgress = 0
    }

    fun resetRecipeIfEmptyInput() {
        if (itemHandler.getStackInSlot(0).isEmpty) {
            resetRecipe()
        }
    }

    override fun buildUI(data: PosGuiData, syncManager: PanelSyncManager): ModularPanel {
        syncManager.syncValue("craftingProgress", SyncHandlers.intNumber({ craftingProgress }, { craftingProgress = it }))
        syncManager.syncValue("requiredProgress", SyncHandlers.intNumber({ requiredProgress }, { requiredProgress = it }))

        val progressWidget: ProgressWidget = ProgressWidget().size(80, 16)
            .texture(ClayGuiTextures.WorkTable.PROGRESS_BAR_EMPTY, 80)
            .progress(this::getNormalizedProgress)
        if (Mods.JustEnoughItems.isModLoaded) {
            progressWidget.addTooltipLine(IKey.lang("jei.tooltip.show.recipes"))
                .listenGuiAction(IGuiAction.MousePressed { _ ->
                    if (!progressWidget.isBelowMouse) return@MousePressed false
                    JeiPlugin.jeiRuntime.recipesGui.showCategories(listOf(ClayWorkTableRecipeCategory.UID))
                    return@MousePressed true
                })
        }

        return ModularPanel.defaultPanel("clay_work_table")
            .child(Flow.column().margin(7).sizeRel(1f)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang("tile.clayium.clay_work_table.name").asWidget().align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
                    .child(MuiSlots.itemSlotBuilder(this.itemHandler, INPUT_SLOT)
                        .singletonSlotGroup()
                        .buildLarge()
                        .left(5).top(18))
                    .child(MuiSlots.itemSlotBuilder(this.itemHandler, OUTPUT1_SLOT)
                        .takeOnly().singletonSlotGroup()
                        .buildLarge()
                        .right(5).top(18))
                    .child(MuiSlots.itemSlotBuilder(this.itemHandler, OUTPUT2_SLOT)
                        .takeOnly().singletonSlotGroup()
                        .build()
                        .right(9).top(47)
                    )
                    .child(MuiSlots.itemSlotBuilder(this.itemHandler, TOOL_SLOT)
                        .singletonSlotGroup(SlotGroup.STORAGE_SLOT_PRIO + 1)
                        // TODO: Use capability
                        .filter { s -> ClayWorkTableMethod.entries.any { s.item in it.requiredTools } }
                        .build()
                        .right(72).top(9))
                    .child(progressWidget.pos(41, 22))
                    .child(Flow.row().size(80, 16).pos(33, 45)
                        .also {
                            for (i in 0..<6) {
                                it.child(ButtonToggleable()
                                    .size(16, 16)
                                    .clickableIf { canPushButton(i) }
                                    .background(ClayGuiTextures.WorkTable.LIST[i].enabled)
                                    .hoverBackground(ClayGuiTextures.WorkTable.LIST[i].hovered)
                                    .unclickableBackground(ClayGuiTextures.WorkTable.LIST[i].disabled)
                                    .syncHandler(InteractionSyncHandler()
                                        .setOnMousePressed { mouse ->
                                            if (!mouse.isClient && canPushButton(i)) {
                                                pushButton(data.player, i)
                                            }
                                        })
                                )
                            }
                        }
                    )
                )
                .child(MuiSlots.playerInventory(0))
            )
    }

    companion object {
        private const val INPUT_SLOT = 0
        private const val TOOL_SLOT = 1
        private const val OUTPUT1_SLOT = 2
        private const val OUTPUT2_SLOT = 3

        private val ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}
