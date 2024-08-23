package com.github.trc.clayium.common.metatileentities.multiblock

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.utils.NumberFormat
import com.cleanroommc.modularui.utils.serialization.ByteBufAdapters
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.AbstractRecipeLogic
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trc.clayium.api.capability.impl.NotifiableItemStackHandler
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockLogic
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockLogic.StructureValidationResult
import com.github.trc.clayium.api.metatileentity.multiblock.MultiblockLogic.StructureValidationResult.Invalid
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.api.util.toList
import com.github.trc.clayium.common.blocks.BlockCaReactorCoil
import com.github.trc.clayium.common.blocks.BlockCaReactorHull
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.registry.CaReactorRecipeRegistry
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.pow

class CaReactorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModesLists[1], validOutputModesLists[1],
    "machine.${CValues.MOD_ID}.ca_reactor", caReactorRegistry) {

    @Suppress("Unused") private val ioHandler = AutoIoHandler.Combined(this)
    private val multiblockLogic = MultiblockLogic(this, ::checkStructure)

    fun getFaceInvalid(): ResourceLocation = clayiumId("blocks/ca_reactor_core_invalid")
    fun getFaceValid() = clayiumId("blocks/ca_reactor_core_valid")
    override val faceTexture get() = if (multiblockLogic.structureFormed) getFaceValid() else getFaceInvalid()
    override val requiredTextures get() = listOf(getFaceValid(), getFaceInvalid())

    override val importItems = NotifiableItemStackHandler(this, 1, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)


    private var avgHullRank = 0
    private var hullCount = 0
    private var efficiency = 0.0
    private var cePerTickMultiplier = 0.0

    /**
     * if the structure is invalid, the reason should be stored here.
     * null if the structure is valid.
     */
    private var errorMsg: Pair<String, Array<String>?>? = null

    private val supportedHullTierRange = when (tier.numeric) {
        10 -> 1..2
        11 -> 1..6
        12, 13 -> 1..10
        else -> IntRange.EMPTY
    }

    override val workable: AbstractRecipeLogic = CaReactorRecipeLogic()

    @Suppress("unused") // to use as a method reference in MultiblockTrait
    private fun checkStructure(handler: MultiblockLogic): StructureValidationResult {
        val hullRanks = IntArrayList()

        val pos = pos ?: return Invalid
        val world = world ?: return Invalid

        val firstCoilPos = EnumFacing.entries.firstNotNullOfOrNull {
            if (world.getBlockState(pos.offset(it)).block is BlockCaReactorCoil) pos.offset(it) else null
        }
        if (firstCoilPos == null) {
            errorMsg = Pair("message.clayium.ca_reactor.no_near_coil", null)
            return Invalid
        }
        val parts = mutableListOf<IMultiblockPart>()
        val coilsWalked = mutableSetOf<BlockPos>()
        val isValid = searchAndValidateAdjacentCoil(firstCoilPos, coilsWalked)
        if (!isValid) return Invalid

        // Extra checks
        // Coil is surrounded by coils or hulls or MBPart and count the hulls
        // Interfaces and coil blocks of a lower tier than the core block cannot be used.
        val hullsWalked = mutableSetOf<BlockPos>()
        for (pos in coilsWalked) {
            for (side in EnumFacing.entries) {
                val pos = pos.offset(side)
                if (hullsWalked.contains(pos)) continue
                hullsWalked.add(pos)
                val metaTileEntity = world.getMetaTileEntity(pos)
                val block = world.getBlockState(pos).block
                when {
                    metaTileEntity is IMultiblockPart -> {
                        if (metaTileEntity.tier.numeric < this.tier.numeric) {
                            errorMsg = Pair("message.clayium.ca_reactor.insufficient_tier_interface", arrayOf(pos.toString()))
                            return Invalid
                        }
                        parts.add(metaTileEntity)
                    }
                    metaTileEntity === this -> {}
                    block is BlockCaReactorHull -> {
                        val hullRank = block.getCaRank(world, pos)
                        if (hullRank !in supportedHullTierRange) {
                            errorMsg = Pair("message.clayium.ca_reactor.too_high_tier_hull", arrayOf(pos.toString()))
                            return Invalid
                        }
                        hullRanks.add(hullRank)
                    }
                    block is BlockCaReactorCoil -> {
                        if (block.getTier(world, pos).numeric < this.tier.numeric) {
                            errorMsg = Pair("message.clayium.ca_reactor.insufficient_tier_coil", arrayOf(pos.toString()))
                            return Invalid
                        }
                    }
                    else -> return Invalid
                }
            }
        }

        if (hullRanks.size < REQUIRED_HULLS) return Invalid

        this.avgHullRank = if (hullRanks.isEmpty) 0 else hullRanks.average().toInt()
        this.hullCount = hullRanks.size

        this.efficiency = getEfficiency(avgHullRank.toDouble(), hullRanks.size)
        this.cePerTickMultiplier = getCEPerTickMultiplier(avgHullRank.toDouble(), hullRanks.size)

        return StructureValidationResult.Valid(parts, emptyList())
    }

    private fun searchAndValidateAdjacentCoil(coilPos: BlockPos, walked: MutableSet<BlockPos>): Boolean {
        if (walked.contains(coilPos)) return true
        if (walked.size > MAX_COILS) {
            errorMsg = Pair("message.clayium.ca_reactor.too_many_coils", arrayOf(MAX_COILS.toString()))
            return false
        }
        walked.add(coilPos)
        val world = world ?: return false
        val coilPoses = mutableListOf<BlockPos>()
        var valid = true
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    if (dx == 0 && dy == 0 && dz == 0) continue
                    val pos = coilPos.add(dx, dy, dz)
                    val block = world.getBlockState(pos).block
                    if (block is BlockCaReactorCoil) {
                        coilPoses.add(pos)
                    }
                }
            }
        }
        if (coilPoses.size == 2) {
            for (neighborCoilPos in coilPoses) {
                valid = valid && searchAndValidateAdjacentCoil(neighborCoilPos, walked)
            }
        } else {
            errorMsg = Pair("message.clayium.ca_reactor.invalid_coil", arrayOf(coilPos.toString()))
            valid = false
        }
        return valid
    }

    override fun createMetaTileEntity() = CaReactorMetaTileEntity(metaTileEntityId, tier)

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "ca_reactor")
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        syncManager.syncValue("caReactorEfficiency", SyncHandlers.doubleNumber(::efficiency, ::efficiency::set))
        syncManager.syncValue("caReactorAvgHullRank", SyncHandlers.intNumber(::avgHullRank, ::avgHullRank::set))
        syncManager.syncValue("caReactorHullCount", SyncHandlers.intNumber(::hullCount, ::hullCount::set))
        syncManager.syncValue("caReactorErrorMsg", SyncHandlers.string({ errorMsg?.first }, { errorMsg = Pair(it, errorMsg?.second) }))
        syncManager.syncValue("caReactorErrorMsgArgs", GenericListSyncHandler(
            { errorMsg?.second?.toList() ?: emptyList() },
            {
                errorMsg = if (errorMsg != null) {
                    Pair(errorMsg!!.first, it.toTypedArray())
                } else {
                    null
                }
            },
            ByteBufAdapters.STRING)
        )

        val errorMsgDrawable = IKey.dynamic {
            val msg = errorMsg
            if (msg != null) {
                I18n.format(msg.first, *msg.second ?: emptyArray())
            } else {
                I18n.format("message.clayium.ca_reactor.valid")
            }
        }

        return super.buildMainParentWidget(syncManager)
            .child(IKey.dynamic {
                if (multiblockLogic.structureFormed)
                    I18n.format("gui.clayium.ca_reactor.constructed")
                else
                    I18n.format("gui.clayium.ca_reactor.invalid") }
                .asWidget().widthRel(0.7f).alignment(Alignment.CenterRight).align(Alignment.BottomRight)
                .tooltip { it.addLine(errorMsgDrawable) }
            )
            .child(IKey.dynamic { I18n.format("gui.clayium.ca_reactor.efficiency", NumberFormat.formatWithMaxDigits(efficiency)) }
                .asWidget().widthRel(0.6f).alignment(Alignment.CenterRight).right(0).bottom(10)
            )
            .child(IKey.dynamic { I18n.format("gui.clayium.ca_reactor.rank_size", avgHullRank, hullCount) }
                .asWidget().widthRel(0.6f).left(0).top(10))
    }

    companion object {
        const val MAX_COILS = 128
        const val REQUIRED_HULLS = 50
        const val EFFICIENCY_BASE = 7.5
        const val CE_CONSUMPTION_MUL_BASE = 1.01
        const val EFFICIENCY_MULTIPLIER = 0.2
        val caReactorRegistry = CaReactorRecipeRegistry("ca_reactor")

        private fun getEfficiency(averageRank: Double, hullCount: Int): Double {
            return EFFICIENCY_MULTIPLIER * EFFICIENCY_BASE.pow(averageRank - 1) * 1.02.pow(hullCount)
        }

        private fun getCEPerTickMultiplier(averageRank: Double, hullCount: Int): Double {
            return CE_CONSUMPTION_MUL_BASE.pow((averageRank - 1) * hullCount)
        }
    }

    private inner class CaReactorRecipeLogic : MultiblockRecipeLogic(this@CaReactorMetaTileEntity, caReactorRegistry, multiblockLogic) {
        override fun trySearchNewRecipe() {
            val recipe = caReactorRegistry.findRecipeWithRank(tier.numeric, avgHullRank, inputInventory.toList())
            if (recipe == null) {
                invalidInputsForRecipes = true
                return
            }
            val duration = (recipe.duration / efficiency).toLong()
            val cePerTick = ClayEnergy((recipe.cePerTick.energy * cePerTickMultiplier).toLong())
            val multipliedRecipe = Recipe(recipe.inputs, recipe.outputs, recipe.chancedOutputs,
                duration, cePerTick, recipe.recipeTier)
            prepareRecipe(multipliedRecipe)
        }
    }
}