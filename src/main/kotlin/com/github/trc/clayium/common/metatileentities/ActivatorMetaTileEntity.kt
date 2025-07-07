package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.ToggleButton
import com.cleanroommc.modularui.widgets.layout.Grid
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MteRenderingConfig
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.util.RayTraceMemory
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.entity.Entity
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.GameType
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.items.ItemHandlerHelper

open class ActivatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "activator", bufferValidInputModes) {

    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)

    override val rangeRelative: Cuboid6 get() = Cuboid6.full.copy().add(this.pos?.offset(this.frontFacing.opposite) ?: BlockPos.ORIGIN)
    override val maxBlocksPerTick = 1

    protected var blockEntityMode = BlockEntityMode.BLOCK
    protected var raytrace = false
    protected var sneaking = false

    private var isBlockForBlockAndEntityMode = true

    protected val scannedEntities = mutableListOf<Entity>()

    override fun drawEnergy(accelerationRate: Double): Boolean { return true }

    override fun getNextBlockPos(): BlockPos? {
        return this.pos?.offset(this.frontFacing.opposite)
    }

    override fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double {
        return 400.0
    }

    override fun getAccelerationRate(): Double {
        return 1.0
    }

    override fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        val clickPos = getNextBlockPos() ?: return EnumActionResult.FAIL
        val world = this.world as? WorldServer ?: return EnumActionResult.FAIL

        when (blockEntityMode) {
            BlockEntityMode.BLOCK ->
                if (this.raytrace)
                    this.clickBlock(world, clickPos, RayTraceMemory.getByFacing(this.frontFacing.opposite))
                else
                    this.rayTraceBlock(world, clickPos, RayTraceMemory.getByFacing(this.frontFacing.opposite))
            BlockEntityMode.ENTITY -> this.interactEntity(world, clickPos)
            BlockEntityMode.BLOCK_AND_ENTITY -> {
                if (this.isBlockForBlockAndEntityMode) {
                    this.clickBlock(world, clickPos, RayTraceMemory.getByFacing(this.frontFacing.opposite))
                    this.isBlockForBlockAndEntityMode = false
                } else {
                    this.interactEntity(world, clickPos)
                    this.isBlockForBlockAndEntityMode = true
                }
            }
        }
        return EnumActionResult.SUCCESS
    }

    protected fun clickBlock(world: World, clickPos: BlockPos, memory: RayTraceMemory) {
        val pos = this.pos ?: return
        val world = world as? WorldServer ?: return
        val filterMatches = filter?.testBlock(world, clickPos) ?: true
        if (!filterMatches) return

        val heldItem = extractHeldItem()

        val player = CUtils.getFakePlayerWithItem(world, heldItem)

        player.setWorld(world)
        player.setLocationAndAngles(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 0f, 0f)
        player.isSneaking = sneaking

        player.interactionManager.processRightClickBlock(
            player, world, heldItem, EnumHand.MAIN_HAND, clickPos,
            memory.side.opposite, memory.hit.x.toFloat(), memory.hit.y.toFloat(), memory.hit.z.toFloat(),
        )

        toMachineInventory(player.inventory)
        return
    }

    protected fun rayTraceBlock(world: World, from: BlockPos, memory: RayTraceMemory) {
        val world = world as? WorldServer ?: return
        val result = memory.rayTraceBlockFrom(
            world, from,
            stopOnLiquid = false,
            ignoreBlockWithoutBoundingBox = false,
            returnLastUncollidableBlock = false
        )
        val passFilter = filter == null || filter?.testBlock(world, from) == true
        if (result != null && passFilter) {
            val heldItem = extractHeldItem()
            val player = CUtils.getFakePlayerWithItem(world, heldItem)
                .apply { this.interactionManager.gameType = GameType.SURVIVAL }
            // subtract eyeHeight because the player is "standing" on the block.
            // If you don't subtract eyeHeight, then it will be higher than this activator block's y coordinate.
            val playerY = from.y.toDouble() - player.eyeHeight
            val playerPos = memory.entityRelPos.add(from.x.toDouble(), from.y.toDouble() - player.eyeHeight, from.z.toDouble())
            player.setWorld(world)
            player.setLocationAndAngles(playerPos.x, playerPos.y, playerPos.z, memory.yaw.toFloat(), memory.pitch.toFloat())
            player.isSneaking = sneaking
            val itemUseResult = player.interactionManager.processRightClick(
                player, world, heldItem, EnumHand.MAIN_HAND,
            )
            if (itemUseResult == null || itemUseResult == EnumActionResult.PASS || itemUseResult == EnumActionResult.FAIL) {
                player.interactionManager.processRightClickBlock(
                    player, world, heldItem, EnumHand.MAIN_HAND, result.blockPos,
                    memory.side, memory.hit.x.toFloat(), memory.hit.y.toFloat(), memory.hit.z.toFloat(),
                )
            }
            toMachineInventory(player.inventory)
        }
    }

    protected fun interactEntity(world: World, clickPos: BlockPos) {
        if (inventoryCrowded()) return
        val pos = this.pos ?: return
        val world = this.world as? WorldServer ?: return

        val heldItem = extractHeldItem()

        val player = CUtils.getFakePlayerWithItem(world, heldItem)

        val entities = world.getEntitiesWithinAABB(Entity::class.java, this.rangeRelative.aabb()) { !scannedEntities.contains(it) }
        if (entities.isEmpty()) {
            scannedEntities.clear()
            return
        }
        player.interactOn(entities.first(), EnumHand.MAIN_HAND)
        toMachineInventory(player.inventory)
        return
    }

    protected fun extractHeldItem(): ItemStack {
        return (0..<this.itemInventory.slots).firstNotNullOfOrNull { i ->
            val stack = this.itemInventory.getStackInSlot(i)
            if (stack.isEmpty) null else this.itemInventory.extractItem(i, Int.MAX_VALUE, false)
        } ?: ItemStack.EMPTY
    }

    private fun toMachineInventory(inventoryPlayer: InventoryPlayer) {
        val world = this.world ?: return
        val pos = this.pos?.offset(this.frontFacing) ?: return
        val remains = mutableListOf<ItemStack>()
        listOf(inventoryPlayer.offHandInventory, inventoryPlayer.mainInventory, inventoryPlayer.armorInventory)
            .flatten()
            .filter { !it.isEmpty }
            .forEach {
                val remain = ItemHandlerHelper.insertItemStacked(this.itemInventory, it, false)
                if (!remain.isEmpty) remains.add(remain)
            }
        for (stack in remains) {
            Block.spawnAsEntity(world, pos, stack)
        }
    }

    private fun inventoryCrowded(): Boolean {
        return !(0..<this.itemInventory.slots).any { this.itemInventory.getStackInSlot(it).isEmpty }
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        val blockEntityButton = CycleButtonWidget()
            .length(3)
            .value(SyncHandlers.enumValue(BlockEntityMode::class.java, ::blockEntityMode, ::blockEntityMode::set))
            .stateBackground(BlockEntityMode.BLOCK, ClayGuiTextures.Clicker.BLOCK)
            .stateHoverBackground(BlockEntityMode.BLOCK, ClayGuiTextures.Clicker.BLOCK_HOVERED)
            .stateBackground(BlockEntityMode.ENTITY, ClayGuiTextures.Clicker.ENTITY)
            .stateHoverBackground(BlockEntityMode.ENTITY, ClayGuiTextures.Clicker.ENTITY_HOVERED)
            .stateBackground(BlockEntityMode.BLOCK_AND_ENTITY, ClayGuiTextures.Clicker.BLOCK_AND_ENTITY)
            .stateHoverBackground(BlockEntityMode.BLOCK_AND_ENTITY, ClayGuiTextures.Clicker.BLOCK_AND_ENTITY_HOVERED)
            .tooltip(0) { it.addLine(IKey.lang("gui.clayium.activator.click_mode.block")) }
            .tooltip(1) { it.addLine(IKey.lang("gui.clayium.activator.click_mode.entity")) }
            .tooltip(2) { it.addLine(IKey.lang("gui.clayium.activator.click_mode.both")) }
        val raytraceButton = ToggleButton()
            .value(SyncHandlers.bool(::raytrace, ::raytrace::set))
            .background(ClayGuiTextures.Clicker.FIXED_TARGET)
            .hoverBackground(ClayGuiTextures.Clicker.FIXED_TARGET_HOVERED)
            .selectedBackground(ClayGuiTextures.Clicker.RAYTRACE)
            .selectedHoverBackground(ClayGuiTextures.Clicker.RAYTRACE_HOVERED)
            .tooltip(false) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_enabled")) }
            .tooltip(true) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_disabled")) }
        val sneakingButton = ToggleButton()
            .value(SyncHandlers.bool(::sneaking, ::sneaking::set))
            .background(ClayGuiTextures.Clicker.NO_SNEAK)
            .hoverBackground(ClayGuiTextures.Clicker.NO_SNEAK_HOVERED)
            .selectedBackground(ClayGuiTextures.Clicker.SNEAK)
            .selectedHoverBackground(ClayGuiTextures.Clicker.SNEAK_HOVERED)

        return super.buildMainParentWidget(syncManager)
            .child(Grid().coverChildren()
                .row(blockEntityButton)
                .row(raytraceButton)
                .row(sneakingButton)
                .minElementMargin(1, 1)
                .right(4).top(12)
            )
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setInteger("blockEntityMode", blockEntityMode.ordinal)
        data.setBoolean("raytrace", raytrace)
        data.setBoolean("sneaking", sneaking)
        data.setBoolean("isBlockForBlockAndEntityMode", isBlockForBlockAndEntityMode)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        blockEntityMode = BlockEntityMode.entries.getOrElse(data.getInteger("blockEntityMode")) { BlockEntityMode.BLOCK }
        raytrace = data.getBoolean("raytrace")
        sneaking = data.getBoolean("sneaking")
        isBlockForBlockAndEntityMode = data.getBoolean("isBlockForBlockAndEntityMode")
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ActivatorMetaTileEntity(metaTileEntityId, tier)
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/areaactivator"))
    }

    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        super.overlayQuads(quads, state, side, rand)
        if (state == null || side == null || state !is IExtendedBlockState) return
        if (side == this.frontFacing.opposite) {
            quads.add(MINER_BACK[side.index])
        }
    }

    enum class BlockEntityMode {
        BLOCK, ENTITY, BLOCK_AND_ENTITY,
    }
}