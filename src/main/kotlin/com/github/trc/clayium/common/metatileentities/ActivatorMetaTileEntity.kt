package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.ToggleButton
import com.cleanroommc.modularui.widgets.layout.Grid
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.metatileentity.AbstractBuilderMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MteRenderingConfig
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getCapability
import com.github.trc.clayium.api.util.hasCapability
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.metatileentities.ActivatorMetaTileEntity.BlockEntityMode.BLOCK
import com.github.trc.clayium.common.metatileentities.ActivatorMetaTileEntity.BlockEntityMode.BLOCK_AND_ENTITY
import com.github.trc.clayium.common.metatileentities.ActivatorMetaTileEntity.BlockEntityMode.ENTITY
import com.github.trc.clayium.common.util.RayTraceMemory
import com.github.trc.clayium.integration.modularui.MuiSlots
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.items.ItemHandlerHelper

open class ActivatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    machineName: String,
    renderMinerBack: Boolean,
) : AbstractBuilderMetaTileEntity(metaTileEntityId, tier, machineName, bufferValidInputModes, renderMinerBack = renderMinerBack) {

    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)

    override val rangeRelativeClient: Cuboid6? get() = Cuboid6.full.copy().add(this.pos?.offset(this.frontFacing.opposite) ?: BlockPos.ORIGIN)
    override val maxBlocksPerTick = 1

    protected val filtersHandler = ClayiumItemStackHandler(this, 2)
    protected val blockFilter: IItemFilter?
        get() = filtersHandler.getStackInSlot(0).getCapability(ClayiumCapabilities.ITEM_FILTER)
    protected val itemFilter: IItemFilter?
        get() = filtersHandler.getStackInSlot(1).getCapability(ClayiumCapabilities.ITEM_FILTER)

    protected var blockEntityMode = BLOCK
    protected var enableRayTrace = false
    protected var sneaking = false

    protected var isBlockForBlockAndEntityMode = true
    protected var allBlocksProcessed = false

    protected val scannedEntities = mutableSetOf<Entity>()

    override fun onPlacement() {
        if (this.frontFacing.axis == EnumFacing.Axis.Y) {
            this.setInput(EnumFacing.SOUTH, MachineIoMode.ALL)
        } else {
            this.setInput(EnumFacing.UP, MachineIoMode.ALL)
        }
        super.onPlacement()
    }

    override fun isFacingValid(facing: EnumFacing) = true

    override fun drawEnergy(accelerationRate: Double): Boolean { return true }

    override fun getNextBlockPos(): BlockPos? {
        this.allBlocksProcessed = true
        return this.pos?.offset(this.frontFacing.opposite)
    }

    override fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double {
        return 400.0
    }

    override fun getAccelerationRate(): Double {
        return 1.0
    }

    override fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        if (inventoryCrowded()) return EnumActionResult.FAIL
        val memory = RayTraceMemory.getByFacing(this.frontFacing.opposite)
        val range = AxisAlignedBB(pos)
        this.doWork(world, pos, memory, range)
        return EnumActionResult.SUCCESS
    }

    protected fun doWork(world: World, pos: BlockPos, memory: RayTraceMemory, rangeAabb: AxisAlignedBB) {
        if (this.enableRayTrace) {
            when (blockEntityMode) {
                BLOCK -> this.rayTraceBlock(world, pos, memory)
                ENTITY -> this.rayTraceEntity(world, pos, memory)
                BLOCK_AND_ENTITY -> this.rayTraceAny(world, pos, memory)
            }
        } else {
            when (blockEntityMode) {
                BLOCK -> this.clickBlock(world, pos, memory)
                ENTITY -> this.clickEntity(world, rangeAabb)
                BLOCK_AND_ENTITY -> {
                    if (this.isBlockForBlockAndEntityMode) {
                        this.clickBlock(world, pos, memory)
                        if (this.allBlocksProcessed) {
                            this.isBlockForBlockAndEntityMode = false
                        }
                    } else {
                        this.clickEntity(world, rangeAabb)
                        val allEntitiesProcessed = this.scannedEntities.isEmpty()
                        if (allEntitiesProcessed) {
                            this.isBlockForBlockAndEntityMode = true
                        }
                    }
                }
            }
        }
    }

    protected fun clickBlock(world: World, clickPos: BlockPos, memory: RayTraceMemory) {
        val pos = this.pos ?: return
        val world = world as? WorldServer ?: return
        val filterMatches = blockFilter?.testBlock(world, clickPos) ?: true
        if (!filterMatches) return

        val heldItem = extractHeldItem()

        val player = CUtils.getFakeSurvivalPlayerWithItem(world, heldItem)

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

    protected fun rayTraceAny(world: World, from: BlockPos, memory: RayTraceMemory) {
        val world = world as? WorldServer ?: return
        val entityRayTraceResult = memory.rayTraceEntityFrom(world, from)
        val blockRayTraceResult = memory.rayTraceBlockFrom(
            world, from,
            stopOnLiquid = false,
            ignoreBlockWithoutBoundingBox = false,
            returnLastUncollidableBlock = false
        )
        val bothNotNull = entityRayTraceResult != null && blockRayTraceResult != null
        val fromVec3d = Vec3d(from)
        val result = if (bothNotNull) {
            val entityDistance = fromVec3d.distanceTo(entityRayTraceResult.hitVec)
            val blockDistance = fromVec3d.distanceTo(blockRayTraceResult.hitVec)
            if (entityDistance < blockDistance) {
                entityRayTraceResult
            } else {
                blockRayTraceResult
            }
        } else {
            entityRayTraceResult ?: blockRayTraceResult ?: return
        }
        when (result.typeOfHit) {
            RayTraceResult.Type.MISS -> {}
            RayTraceResult.Type.BLOCK -> this.clickBlockUsingRayTraceResult(world, from, memory, result)
            RayTraceResult.Type.ENTITY -> this.interactOn(world, result.entityHit)
        }
    }

    protected fun rayTraceBlock(world: World, from: BlockPos, memory: RayTraceMemory) {
        val world = world as? WorldServer ?: return
        val result = memory.rayTraceBlockFrom(
            world, from,
            stopOnLiquid = false,
            ignoreBlockWithoutBoundingBox = false,
            returnLastUncollidableBlock = false
        )
        if (result != null) {
            this.clickBlockUsingRayTraceResult(world, from, memory, result)
        }
    }

    protected fun clickBlockUsingRayTraceResult(world: World, from: BlockPos, memory: RayTraceMemory, result: RayTraceResult) {
        val world = world as? WorldServer ?: return
        val pos: BlockPos = result.blockPos
        val blockFilter = this.blockFilter
        val passFilter = blockFilter == null || blockFilter.testBlock(world, pos)
        if (!passFilter) return

        val heldItem = extractHeldItem()
        val player = CUtils.getFakeSurvivalPlayerWithItem(world, heldItem)
        // subtract eyeHeight because the player is "standing" on the block.
        // If you don't subtract eyeHeight, then it will be higher than this activator block's y coordinate.
        val playerY = from.y.toDouble() - player.eyeHeight
        val playerPos = memory.entityRelPos.add(from.x.toDouble(), playerY, from.z.toDouble())
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

    protected fun clickEntity(world: World, aabb: AxisAlignedBB) {
        val world = world as? WorldServer ?: return

        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { !scannedEntities.contains(it) }
        if (entities.isEmpty()) {
            this.scannedEntities.clear()
        } else {
            val entity = entities.first()
            this.scannedEntities.add(entity)
            interactOn(world, entity)
        }
    }

    protected fun rayTraceEntity(world: World, from: BlockPos, memory: RayTraceMemory) {
        val world = world as? WorldServer ?: return
        val result = memory.rayTraceEntityFrom(world, from)
        if (result == null || result.entityHit == null) return
        val entity: Entity = result.entityHit
        interactOn(world, entity)
    }

    protected fun interactOn(world: WorldServer, entity: Entity) {
        val heldItem = extractHeldItem()
        val player = CUtils.getFakeSurvivalPlayerWithItem(world, heldItem)
        player.interactOn(entity, EnumHand.MAIN_HAND)
        toMachineInventory(player.inventory)
    }

    protected fun extractHeldItem(): ItemStack {
        return (0..<this.itemInventory.slots).firstNotNullOfOrNull { i ->
            val stack = this.itemInventory.getStackInSlot(i)
            val itemFilter = this.itemFilter
            val filterPass = itemFilter == null || itemFilter.test(stack)
            if (stack.isEmpty || !filterPass) null else this.itemInventory.extractItem(i, Int.MAX_VALUE, false)
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
            .stateBackground(BLOCK, ClayGuiTextures.Clicker.BLOCK)
            .stateHoverBackground(BLOCK, ClayGuiTextures.Clicker.BLOCK_HOVERED)
            .stateBackground(ENTITY, ClayGuiTextures.Clicker.ENTITY)
            .stateHoverBackground(ENTITY, ClayGuiTextures.Clicker.ENTITY_HOVERED)
            .stateBackground(BLOCK_AND_ENTITY, ClayGuiTextures.Clicker.BLOCK_AND_ENTITY)
            .stateHoverBackground(BLOCK_AND_ENTITY, ClayGuiTextures.Clicker.BLOCK_AND_ENTITY_HOVERED)
        val raytraceButton = ToggleButton()
            .value(SyncHandlers.bool(::enableRayTrace, ::enableRayTrace::set))
            .background(ClayGuiTextures.Clicker.FIXED_TARGET)
            .hoverBackground(ClayGuiTextures.Clicker.FIXED_TARGET_HOVERED)
            .selectedBackground(ClayGuiTextures.Clicker.RAYTRACE)
            .selectedHoverBackground(ClayGuiTextures.Clicker.RAYTRACE_HOVERED)
            .tooltip(false) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_disabled")) }
            .tooltip(true) { it.addLine(IKey.lang("gui.clayium.activator.raytrace_enabled")) }
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
            .child(MuiSlots.phantomSlotBuilder(filtersHandler, 0).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) }.build()
                .background(ClayGuiTextures.FILTER_SLOT)
                .top(12).right(32)
                .tooltipBuilder { it.addLine(IKey.lang("gui.clayium.activator.block_filter")) }
            )
            .child(MuiSlots.phantomSlotBuilder(filtersHandler, 1).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) }.build()
                .background(ClayGuiTextures.FILTER_SLOT)
                .top(12 + 18).right(32)
                .tooltipBuilder { it.addLine(IKey.lang("gui.clayium.activator.item_filter")) }
            )
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setInteger("blockEntityMode", blockEntityMode.ordinal)
        data.setBoolean("raytrace", enableRayTrace)
        data.setBoolean("sneaking", sneaking)
        data.setBoolean("isBlockForBlockAndEntityMode", isBlockForBlockAndEntityMode)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        blockEntityMode = BlockEntityMode.entries.getOrElse(data.getInteger("blockEntityMode")) { BLOCK }
        enableRayTrace = data.getBoolean("raytrace")
        sneaking = data.getBoolean("sneaking")
        isBlockForBlockAndEntityMode = data.getBoolean("isBlockForBlockAndEntityMode")
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ActivatorMetaTileEntity(metaTileEntityId, tier, "activator", renderMinerBack = true)
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/activator"))
    }

    enum class BlockEntityMode {
        BLOCK, ENTITY, BLOCK_AND_ENTITY,
    }
}