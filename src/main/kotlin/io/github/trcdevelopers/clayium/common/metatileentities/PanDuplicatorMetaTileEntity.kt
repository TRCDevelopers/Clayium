package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Row
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.AbstractWorkable
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.pan.IPan
import io.github.trcdevelopers.clayium.api.pan.IPanCable
import io.github.trcdevelopers.clayium.api.pan.IPanUser
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.ItemAndMeta
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.client.model.ModelTextures
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.recipe.ingredient.COreRecipeInput
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import java.util.function.Function
import kotlin.math.pow

class PanDuplicatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    private val duplicatorRank: Int,
    private val machineHullTier: ITier = ClayTiers.entries[duplicatorRank + 3]
) : MetaTileEntity(metaTileEntityId, tier, validInputModesLists[2], validOutputModesLists[1], "pan_duplicator"), IPanUser {

    val maxCeConsumptionRate = ClayEnergy(10_000 * 10.0.pow(duplicatorRank - 1).toLong())

    private val antimatterSlot = NotifiableItemStackHandler(this, 1, this, isExport = false)
    private val duplicationTargetSlot = NotifiableItemStackHandler(this, 1, this, isExport = false)

    override val importItems = CombinedInvWrapper(antimatterSlot, duplicationTargetSlot)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    @Suppress("unused") private val ioHandler = AutoIoHandler.Combined(this)
    private val clayEnergyHolder = ClayEnergyHolder(this)
    private val recipeLogic = PanDuplicatorRecipeLogic()

    private var pan: IPan? = null

    override fun onPlacement() {
        this.setInput(EnumFacing.UP, MachineIoMode.ALL)
        this.setInput(this.frontFacing.opposite, MachineIoMode.CE)
        this.setOutput(EnumFacing.DOWN, MachineIoMode.ALL)
        super.onPlacement()
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.PAN_CABLE -> capability.cast(IPanCable.INSTANCE)
            capability === ClayiumTileCapabilities.PAN_USER -> capability.cast(this)
            else -> super.getCapability(capability, facing)
        }
    }

    override fun itemsDroppedOnDestroy(itemBuffer: MutableList<ItemStack>) {
        super.itemsDroppedOnDestroy(itemBuffer)
        clearInventory(itemBuffer, clayEnergyHolder.energizedClayItemHandler)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return PanDuplicatorMetaTileEntity(metaTileEntityId, tier, duplicatorRank)
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .bottom(12).left(0))
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
            .child(Row().widthRel(0.7f).height(26).align(Alignment.Center)
                .child(SlotGroupWidget.builder()
                    .row("AD")
                    .key('A', MuiSlots.itemSlotBuilder(antimatterSlot, 0).singletonSlotGroup().build()
                        .background(ClayGuiTextures.IMPORT_1_SLOT))
                    .key('D', MuiSlots.itemSlotBuilder(duplicationTargetSlot, 0).singletonSlotGroup().build()
                        .background(ClayGuiTextures.IMPORT_2_SLOT))
                    .build()
                    .align(Alignment.CenterLeft)
                )
                .child(MuiSlots.itemSlotBuilder(exportItems, 0).singletonSlotGroup().takeOnly().buildLarge()
                    .align(Alignment.CenterRight))
                .child(recipeLogic.getProgressBar(syncManager, showRecipes = false)
                    .progress(recipeLogic::getNormalizedProgress)
                    .align(Alignment.Center)
                )
            )
    }

    override fun setNetwork(network: IPan) {
        pan = network
    }

    override fun resetNetwork() {
        pan = null
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            ModelResourceLocation(clayiumId("machines/pan_duplicator"), "rank=$duplicatorRank"))
    }

    @SideOnly(Side.CLIENT)
    override fun getItemStackDisplayName(): String {
        return I18n.format(this.translationKey, this.duplicatorRank)
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add("CE Consumption Rate: ${maxCeConsumptionRate.format()}/t")
    }

    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val sprite = getter.apply(clayiumId("blocks/pan_casing"))
        panCasingQuads = EnumFacing.entries.map { ModelTextures.createQuad(it, sprite) }
    }

    override fun getQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (state == null || side == null) return
        quads.add(ModelTextures.getHullQuads(this.machineHullTier)?.get(side) ?: return)
    }

    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (state == null || side == null) return
        quads.add(panCasingQuads[side.index])
        super.overlayQuads(quads, state, side, rand)
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.builder()
            .face(clayiumId("blocks/pan_duplicator"))
            .particle {
                if (ModelTextures.isInitialized) ModelTextures.getHullTexture(this.machineHullTier) else null
            }
            .build()
    }

    private inner class PanDuplicatorRecipeLogic : AbstractWorkable(this@PanDuplicatorMetaTileEntity) {
        override fun trySearchNewRecipe(): Boolean {
            val hasAntimatter = antimatterInput.testItemStackAndAmount(antimatterSlot.getStackInSlot(0))
            if (!hasAntimatter) return false
            val targetStack = duplicationTargetSlot.getStackInSlot(0)
            if (targetStack.isEmpty) return false
            val duplicationTarget = targetStack.copyWithSize(1)
            val duplicationCost: ClayEnergy = pan?.getDuplicationEntries()[ItemAndMeta(duplicationTarget)] ?: return false

            if (!TransferUtils.insertToHandler(metaTileEntity.exportItems, listOf(duplicationTarget), true)) {
                this.outputsFull = true
                return false
            }

            antimatterSlot.extractItem(0, 1, false)
            this.requiredProgress = duplicationCost.energy
            this.currentProgress = 1
            this.itemOutputs = listOf(duplicationTarget)
            return true
        }

        override fun updateWorkingProgress() {
            val requiredEnergyRemaining = ClayEnergy(requiredProgress - currentProgress + 1) // +1 because we start progress at 1
            val maxConsumption = applyOverclock(maxCeConsumptionRate) * ocHandler.accelerationFactor
            val actualConsumption = ClayEnergy.min(requiredEnergyRemaining, maxConsumption)
            if (!clayEnergyHolder.drawEnergy(actualConsumption, simulate = true)) return

            clayEnergyHolder.drawEnergy(actualConsumption, simulate = false)
            currentProgress += actualConsumption.energy
            if (currentProgress > requiredProgress) {
                completeWork()
            }
        }

        private fun applyOverclock(baseConsumption: ClayEnergy): ClayEnergy {
            // C Factor reduces the crafting time by 1/C,
            // and multiplies the CE Consumption by C^1.5.
            // [!] PAN Duplicators have 100% OC efficiency, so this is the formula for the overclocking.
            val c = ocHandler.compensatedFactor
            return baseConsumption * c * c.pow(1.5)
        }


        @Optional.Method(modid = Mods.Names.THE_ONE_PROBE)
        override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
            if (!isWorking) return
            val energy = ClayEnergy(currentProgress)
            val maxEnergy = ClayEnergy(requiredProgress)

            val color = if (isWorkingEnabled) COLOR_ENABLED_ARGB else COLOR_DISABLED_ARGB
            if (requiredProgress > 0) {
                probeInfo.progress(
                    currentProgress, requiredProgress, probeInfo.defaultProgressStyle()
                        .numberFormat(NumberFormat.NONE)
                        .prefix(energy.format())
                        .suffix(" / ${maxEnergy.format()}")
                        .filledColor(color)
                        .alternateFilledColor(color)
                        .borderColor(BORDER_COLOR)
                )
            }
        }
    }

    companion object {
        private val antimatterInput = COreRecipeInput(OrePrefix.gem, CMaterials.antimatter)

        private lateinit var panCasingQuads: List<BakedQuad>
    }
}

// TOP Info Colors
//TODO: put these variables in somewhere public
private const val COLOR_ENABLED_ARGB: Int = 0xFF4CBB17.toInt()
private const val COLOR_DISABLED_ARGB: Int = 0xFFBB1C28.toInt()
private const val BORDER_COLOR: Int = 0xFF555555.toInt()
