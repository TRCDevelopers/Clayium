package io.github.trcdevelopers.clayium.common.metatileentities.multiblock

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.drawable.GuiTextures
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ButtonWidget
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.CA_REACTOR_HULL_POSES
import io.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import io.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.multiblock.IMultiblockPart
import io.github.trcdevelopers.clayium.api.metatileentity.multiblock.MultiblockLogic
import io.github.trcdevelopers.clayium.api.metatileentity.multiblock.MultiblockLogic.StructureValidationResult
import io.github.trcdevelopers.clayium.api.metatileentity.multiblock.MultiblockLogic.StructureValidationResult.Invalid
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.asWidgetResizing
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.api.util.getMetaTileEntity
import io.github.trcdevelopers.clayium.api.util.toList
import io.github.trcdevelopers.clayium.client.renderer.CRenderUtils
import io.github.trcdevelopers.clayium.common.blocks.BlockCaReactorCoil
import io.github.trcdevelopers.clayium.common.blocks.BlockCaReactorHull
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import io.github.trcdevelopers.clayium.common.recipe.Recipe
import io.github.trcdevelopers.clayium.common.recipe.registry.CaReactorRecipeRegistry
import io.github.trcdevelopers.clayium.common.util.SidelessI18n
import io.github.trcdevelopers.clayium.integration.modularui.CNumFormat
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import org.lwjgl.opengl.GL11
import kotlin.math.pow

class CaReactorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : WorkableMetaTileEntity(metaTileEntityId, tier, caReactorRegistry) {

    @Suppress("Unused") private val ioHandler = AutoIoHandler.Combined(this)
    private val multiblockLogic = MultiblockLogic(this, ::checkStructure)

    override val renderingConfig by lazy {
        val whenValid = clayiumId("blocks/ca_reactor_core_valid")
        val whenInvalid = clayiumId("blocks/ca_reactor_core_invalid")
        MteRenderingConfig.builder()
            .dynFace { if (multiblockLogic.structureFormed) whenValid else whenInvalid }
            .addRequiredTextures(whenValid, whenInvalid)
            .alwaysRender()
            .build()
    }

    override val importItems = NotifiableItemStackHandler(this, 1, this, isExport = false)
    override val exportItems = NotifiableItemStackHandler(this, 1, this, isExport = true)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)

    private var avgHullRank = 0
    private var hullCount = 0
    private var efficiency = 0.0
    private var cePerTickMultiplier = 0.0
    private val hullPoses = mutableListOf<BlockPos>()

    /**
     * if the structure is invalid, the reason should be stored here.
     * null if the structure is valid.
     */
    private var errorMsg: ITextComponent? = null

    private val supportedHullTierRange = when (tier.numeric) {
        10 -> 1..2
        11 -> 1..6
        12, 13 -> 1..10
        else -> IntRange.EMPTY
    }

    override val workable: AbstractRecipeLogic = CaReactorRecipeLogic()

    @Suppress("unused") // to use as a method reference in MultiblockTrait
    private fun checkStructure(handler: MultiblockLogic): StructureValidationResult {
        hullPoses.clear()
        val hullRanks = IntArrayList()

        val pos = pos ?: return Invalid
        val world = world ?: return Invalid

        val firstCoilPos = EnumFacing.entries.firstNotNullOfOrNull {
            if (world.getBlockState(pos.offset(it)).block is BlockCaReactorCoil) pos.offset(it) else null
        }
        if (firstCoilPos == null) {
            errorMsg = TextComponentTranslation("message.clayium.ca_reactor.no_near_coil")
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
        for (coilPos in coilsWalked) {
            for (side in EnumFacing.entries) {
                val pos = coilPos.offset(side)
                if (hullsWalked.contains(pos)) continue
                hullsWalked.add(pos)
                val metaTileEntity = world.getMetaTileEntity(pos)
                val block = world.getBlockState(pos).block
                when {
                    metaTileEntity is IMultiblockPart -> {
                        if (metaTileEntity.tier.numeric < this.tier.numeric) {
                            errorMsg = TextComponentTranslation("message.clayium.ca_reactor.insufficient_tier_interface", pos)
                            return Invalid
                        }
                        parts.add(metaTileEntity)
                    }
                    metaTileEntity === this -> {}
                    block is BlockCaReactorHull -> {
                        val hullRank = block.getCaRank(world, pos)
                        if (hullRank !in supportedHullTierRange) {
                            errorMsg = TextComponentTranslation("message.clayium.ca_reactor.too_high_tier_hull", pos)
                            return Invalid
                        }
                        hullRanks.add(hullRank)
                        hullPoses.add(pos)
                    }
                    block is BlockCaReactorCoil -> {
                        if (block.getTier(world, pos).numeric < this.tier.numeric) {
                            errorMsg = TextComponentTranslation("message.clayium.ca_reactor.insufficient_tier_coil", pos)
                            return Invalid
                        }
                    }
                    else -> {
                        errorMsg = TextComponentTranslation("message.clayium.ca_reactor.invalid_coil", coilPos)
                        return Invalid
                    }
                }
            }
        }

        if (hullRanks.size < REQUIRED_HULLS) return Invalid

        this.avgHullRank = if (hullRanks.isEmpty) 0 else hullRanks.average().toInt()
        this.hullCount = hullRanks.size

        this.efficiency = getEfficiency(avgHullRank.toDouble(), hullRanks.size)
        this.cePerTickMultiplier = getCEPerTickMultiplier(avgHullRank.toDouble(), hullRanks.size)

        writeCustomData(CA_REACTOR_HULL_POSES) {
            writeVarInt(avgHullRank)
            writeVarInt(hullPoses.size)
            for (hullPos in hullPoses) {
                writeBlockPos(hullPos)
            }
        }

        return StructureValidationResult.Valid(parts, emptyList())
    }

    private fun searchAndValidateAdjacentCoil(coilPos: BlockPos, walked: MutableSet<BlockPos>): Boolean {
        if (walked.contains(coilPos)) return true
        if (walked.size > MAX_COILS) {
            errorMsg = TextComponentTranslation("message.clayium.ca_reactor.too_many_coils", MAX_COILS)
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
            errorMsg = TextComponentTranslation("message.clayium.ca_reactor.invalid_coil", coilPos)
            valid = false
        }
        return valid
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == CA_REACTOR_HULL_POSES) {
            this.avgHullRank = buf.readVarInt()
            hullPoses.clear()
            val size = buf.readVarInt()
            for (i in 0..<size) {
                hullPoses.add(buf.readBlockPos())
            }
            return
        }
        super.receiveCustomData(discriminator, buf)
    }

    override fun createMetaTileEntity() = CaReactorMetaTileEntity(metaTileEntityId, tier)

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.syncValue("caReactorEfficiency", SyncHandlers.doubleNumber(::efficiency, ::efficiency::set))
        syncManager.syncValue("caReactorAvgHullRank", SyncHandlers.intNumber(::avgHullRank, ::avgHullRank::set))
        syncManager.syncValue("caReactorHullCount", SyncHandlers.intNumber(::hullCount, ::hullCount::set))

        return super.buildMainParentWidget(syncManager)
            .child(ButtonWidget().height(12).widthRel(0.45f)
                .background(GuiTextures.BUTTON_CLEAN)
                .overlay(IKey.dynamic {
                    if (multiblockLogic.structureFormed)
                        I18n.format("gui.clayium.ca_reactor.constructed")
                    else
                        I18n.format("gui.clayium.ca_reactor.invalid")
                })
                .align(Alignment.BottomRight)
                .syncHandler(InteractionSyncHandler().setOnMousePressed { mouseData ->
                    if (multiblockLogic.structureFormed || mouseData.isClient) return@setOnMousePressed
                    val err = errorMsg ?: return@setOnMousePressed
                    syncManager.player.sendMessage(err)
                })
            )
            .child(IKey.dynamic { SidelessI18n.format("gui.clayium.ca_reactor.efficiency", CNumFormat.format(efficiency)) }
                .asWidgetResizing().alignment(Alignment.CenterRight).alignX(Alignment.BottomRight.x).bottom(14)
            )
            .child(IKey.dynamic { SidelessI18n.format("gui.clayium.ca_reactor.rank_size", avgHullRank, hullCount) }
                .asWidgetResizing().left(0).top(10))
    }

    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        if (!(this.workable.isWorking && ConfigCore.rendering.caReactorGlittering)) return
        for (j in 1..<(avgHullRank + 2)) {
            for (hullPos in hullPoses) {
                val pos = hullPos.subtract(this.pos ?: return)
                val s = 1.0
                val i = avgHullRank + 1.0 - j
                val k = j / (avgHullRank + 1.0)
                val d = 0.01f * (i.pow(1.6) + 1.0)

                val tessellator = Tessellator.getInstance()
                val bufferBuilder = tessellator.buffer

                val aabb = AxisAlignedBB(pos.x - d, pos.y - d, pos.z - d, pos.x + s + d, pos.y + s + d, pos.z + s + d)

                GlStateManager.pushMatrix()
                CRenderUtils.enableTranslucent()

                GlStateManager.translate(x, y, z)

                val r = 1f
                val g = 1f
                val b = (0.3f + 0.05f + (2.0f * k - k * k) * j).toFloat()
                val a = 0.11f

                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
                bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
                tessellator.draw()
                GlStateManager.color(1f, 1f, 1f, 1f)

                CRenderUtils.disableTranslucent()
                GlStateManager.popMatrix()
            }
        }
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
        override fun trySearchNewRecipe(): Boolean {
            val recipe = caReactorRegistry.findRecipeWithRank(tier.numeric, avgHullRank, inputInventory.toList())
            if (recipe == null) {
                invalidInputsForRecipes = true
                return false
            }
            val duration = (recipe.duration / efficiency).toLong()
            val cePerTick = ClayEnergy((recipe.cePerTick.energy * cePerTickMultiplier).toLong())
            val multipliedRecipe = Recipe(recipe.inputs, recipe.outputs, recipe.chancedOutputs,
                duration, cePerTick, recipe.recipeTier)
            return prepareRecipe(multipliedRecipe)
        }
    }
}