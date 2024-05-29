package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.Widget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import com.github.trcdevelopers.clayium.api.CTranslation
import com.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trcdevelopers.clayium.api.capability.impl.RecipeLogicEnergy
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class SimpleMachineMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    override val faceTexture: ResourceLocation,
    recipeRegistry: RecipeRegistry<*>,
    // saved for createMetaTileEntity()
    private val workableProvider: (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic = ::RecipeLogicEnergy,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, recipeRegistry) {

    constructor(
        metaTileEntityId: ResourceLocation,
        tier: ITier,
        recipeRegistry: RecipeRegistry<*>,
        workableProvider: (MetaTileEntity, RecipeRegistry<*>, ClayEnergyHolder) -> AbstractRecipeLogic = ::RecipeLogicEnergy,
    ) : this(
        metaTileEntityId, tier,
        validInputModesLists[recipeRegistry.maxInputs], validOutputModesLists[recipeRegistry.maxOutputs],
        translationKey = "machine.${metaTileEntityId.namespace}.${recipeRegistry.category.categoryName}",
        faceTexture = ResourceLocation(metaTileEntityId.namespace, "blocks/${recipeRegistry.category.categoryName}"),
        recipeRegistry, workableProvider
    )

    override val workable = workableProvider(this, recipeRegistry, clayEnergyHolder)

    override fun createMetaTileEntity(): MetaTileEntity {
        return SimpleMachineMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, faceTexture, recipeRegistry, workableProvider)
    }

    override fun changeIoModesOnPlacement(placer: EntityLivingBase) {
        this._inputModes[EnumFacing.UP.index] = MachineIoMode.ALL
        this.refreshNeighborConnection(EnumFacing.UP)
        this._outputModes[EnumFacing.DOWN.index] = MachineIoMode.ALL
        this.refreshNeighborConnection(EnumFacing.DOWN)
        val placerFacing = placer.horizontalFacing
        this._inputModes[placerFacing.index] = MachineIoMode.CE
        this.refreshNeighborConnection(placerFacing.opposite)
        super.changeIoModesOnPlacement(placer)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        // add the tier-specific tooltip first
        UtilLocale.formatTooltips(tooltip, "machine.clayium.${metaTileEntityId.path}.tooltip")
        // then add the machine-specific tooltip
        UtilLocale.formatTooltips(tooltip, "machine.clayium.${recipeRegistry.category.categoryName}.tooltip")
    }
}