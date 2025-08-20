package io.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.layout.Flow
import io.github.trcdevelopers.clayium.api.ClayiumApi
import io.github.trcdevelopers.clayium.api.block.BlockMachine.Companion.IS_PIPE
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.INITIALIZE_MTE
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.SYNC_MTE_TRAIT
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_CONNECTIONS
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_FRONT_FACING
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_INPUT_MODE
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_OUTPUT_MODE
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.IConfigurationTool
import io.github.trcdevelopers.clayium.api.capability.IConfigurationTool.ToolType.EXTRACTION
import io.github.trcdevelopers.clayium.api.capability.IConfigurationTool.ToolType.FILTER_REMOVER
import io.github.trcdevelopers.clayium.api.capability.IConfigurationTool.ToolType.INSERTION
import io.github.trcdevelopers.clayium.api.capability.IConfigurationTool.ToolType.PIPING
import io.github.trcdevelopers.clayium.api.capability.IConfigurationTool.ToolType.ROTATION
import io.github.trcdevelopers.clayium.api.capability.IPipeConnectable
import io.github.trcdevelopers.clayium.api.capability.IPipeConnectionLogic
import io.github.trcdevelopers.clayium.api.capability.PipeConnectionMode
import io.github.trcdevelopers.clayium.api.capability.impl.FilteredItemHandler
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.capability.impl.RangedItemHandlerProxy
import io.github.trcdevelopers.clayium.api.gui.MetaTileEntityGuiFactory
import io.github.trcdevelopers.clayium.api.gui.data.MetaTileEntityGuiData
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity.Companion.clearInventory
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IWorldObject
import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler
import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.ALL
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.CE
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.FIRST
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.FLUID
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.M_1
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.M_2
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.M_3
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.M_4
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.M_5
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.M_6
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.M_ALL
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.NONE
import io.github.trcdevelopers.clayium.api.util.MachineIoMode.SECOND
import io.github.trcdevelopers.clayium.api.util.asWidgetResizing
import io.github.trcdevelopers.clayium.client.model.ModelTextures
import io.github.trcdevelopers.clayium.common.capability.impl.ItemFilterHolderTrait
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
import io.github.trcdevelopers.clayium.common.util.SidelessI18n
import io.github.trcdevelopers.clayium.common.util.UtilLocale
import io.github.trcdevelopers.clayium.integration.modularui.IGuiHolderClayium
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.MustBeInvokedByOverriders

abstract class MetaTileEntity(
    val metaTileEntityId: ResourceLocation,
    val tier: ITier,
    open val validInputModes: List<MachineIoMode>,
    open val validOutputModes: List<MachineIoMode>,
    /**
     * simple name for this machine, like "bending_machine" or "ranged_miner".
     * used for translation key and item model registration.
     * translation key will be "machine.${metaTileEntityId.namespace}.name".
     * for a translating logic, see [getItemStackDisplayName].
     * item model location will be ("${metaTileEntityId.namespace}:machines/${name}", "tier={tier.lowerName}").
     */
    private val name: String,
) : ISyncedTileEntity, IWorldObject, IGuiHolderClayium<MetaTileEntityGuiData>, IPipeConnectable {

    val mteRegistry = ClayiumApi.mteManager.getRegistry(metaTileEntityId.namespace)
    val blockMachine get() = mteRegistry.blockMachine
    val itemBlockMachine get() = mteRegistry.itemBlockMachine

    val forgeRarity = tier.rarity
    val translationKey = "machine.${metaTileEntityId.namespace}.$name"

    var holder: MetaTileEntityHolder? = null
    val world: World? get() = holder?.world
    val pos: BlockPos? get() = holder?.pos
    val isInvalid get() = holder?.isInvalid ?: true
    val isRemote get() = world?.isRemote ?: true

    override val pipeConnectionLogic: IPipeConnectionLogic = IPipeConnectionLogic.Machine

    // IWorldObj
    override val worldObj: World? get() = world
    override val position: BlockPos? get() = pos


    protected val mteTraits = mutableMapOf<String, MTETrait>()
    protected val traitByNetworkId = Int2ObjectOpenHashMap<MTETrait>()

    abstract val importItems: IItemHandlerModifiable
    abstract val exportItems: IItemHandlerModifiable
    abstract val itemInventory: IItemHandler

    var hasNotifiedInputs = false
    var hasNotifiedOutputs = false

    abstract fun createMetaTileEntity(): MetaTileEntity

    protected val _inputModes = MutableList(6) { NONE }
    protected val _outputModes = MutableList(6) { NONE }
    private val _connectionsCache = BooleanArray(6)
    val inputModes get() = _inputModes.toList()
    val outputModes get() = _outputModes.toList()
    val connectionsCache get() = _connectionsCache.copyOf()

    var frontFacing = EnumFacing.NORTH
        set(value) {
            if (isFacingValid(value)) {
                val syncFlag = !(isRemote || field == value)
                field = value
                markAsDirty()
                if (syncFlag) writeCustomData(UPDATE_FRONT_FACING) { writeByte(value.index) }
            }
        }

    private var timer = 0L
    private val timerOffset = (0..19).random()
    val offsetTimer: Long get() = timer + timerOffset

    val overclockHandler = OverclockHandler(this)
    val overclock: Double get() = overclockHandler.rawOcFactor

    private val filterHolder = ItemFilterHolderTrait(this)

    fun addMetaTileEntityTrait(trait: MTETrait) {
        mteTraits[trait.name] = trait
        traitByNetworkId[trait.networkId] = trait
    }

    override fun markAsDirty() { holder?.markDirty() }

    @MustBeInvokedByOverriders
    open fun update() {
        if (timer == 0L) {
            onFirstTick()
        }
        mteTraits.values.forEach(MTETrait::update)
        timer++
    }

    open fun onFirstTick() {
        mteTraits.values.forEach(MTETrait::onFirstTick)
    }

    /**
     * Ctrl+RClick replacing.
     */
    open fun canBeReplacedTo(world: World, pos: BlockPos, sampleMetaTileEntity: MetaTileEntity): Boolean {
        // shouldn't be replaced if the same MTE.
        if (sampleMetaTileEntity.metaTileEntityId == this.metaTileEntityId) return false
        val thisClass = this::class
        val thatClass = sampleMetaTileEntity::class
        return thisClass == thatClass
    }

    /**
     * Ctrl+RClick replacing.
     * FIXME: Not synced to the client side.
     */
    fun replaceTo(world: World, pos: BlockPos, sampleMetaTileEntity: MetaTileEntity) {
        if (world.isRemote) return
        if (!(world == this.world && pos == this.pos)) return
        val data = NBTTagCompound()
        this.writeToNBT(data)
        val newMetaTileEntity = sampleMetaTileEntity.createMetaTileEntity()
        newMetaTileEntity.readFromNBT(data)
        holder!!.metaTileEntity = newMetaTileEntity
        holder!!.writeCustomData(INITIALIZE_MTE) {
            writeVarInt(mteRegistry.getIdByKey(sampleMetaTileEntity.metaTileEntityId))
            newMetaTileEntity.writeInitialSyncData(this)
        }
        world.neighborChanged(pos, holder!!.blockType, pos)
        markAsDirty()
        Block.spawnAsEntity(world, pos, this.asStackForm())
        this.onReplace(world, pos, newMetaTileEntity, data)
        this.scheduleRenderUpdate()
    }

    /**
     * called when Ctrl+RClick replacing happens.
     */
    protected open fun onReplace(world: World, pos: BlockPos, newMetaTileEntity: MetaTileEntity, oldMteData: NBTTagCompound) {}

    open fun writeToNBT(data: NBTTagCompound) {
        data.setByte("frontFacing", frontFacing.index.toByte())
        data.setByteArray("inputModes", ByteArray(6) { _inputModes[it].id.toByte() })
        data.setByteArray("outputModes", ByteArray(6) { _outputModes[it].id.toByte() })
        data.setByteArray("connections", ByteArray(6) { if (_connectionsCache[it]) 1 else 0 })
        CUtils.writeItems(importItems, IMPORT_INVENTORY, data)
        CUtils.writeItems(exportItems, EXPORT_INVENTORY, data)
        for ((name, trait) in mteTraits) {
            data.setTag(name, trait.serializeNBT())
        }
    }

    open fun readFromNBT(data: NBTTagCompound) {
        frontFacing = EnumFacing.byIndex(data.getByte("frontFacing").toInt())
        data.getByteArray("inputModes").forEachIndexed { i, id -> _inputModes[i] = MachineIoMode.byId(id.toInt()) }
        data.getByteArray("outputModes").forEachIndexed { i, id -> _outputModes[i] = MachineIoMode.byId(id.toInt()) }
        data.getByteArray("connections").forEachIndexed { i, b -> _connectionsCache[i] = (b == 1.toByte()) }
        CUtils.readItems(importItems, "importInventory", data)
        CUtils.readItems(exportItems, "exportInventory", data)
        for ((name, trait) in mteTraits) {
            trait.deserializeNBT(data.getCompoundTag(name))
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        buf.writeByte(frontFacing.index)
        for (i in 0..5) {
            buf.writeByte(_inputModes[i].id)
            buf.writeByte(_outputModes[i].id)
            buf.writeBoolean(_connectionsCache[i])
        }
        buf.writeVarInt(traitByNetworkId.size)
        for ((id, trait) in traitByNetworkId) {
            buf.writeVarInt(id)
            trait.writeInitialSyncData(buf)
        }
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
        for (i in 0..5) {
            _inputModes[i] = MachineIoMode.byId(buf.readByte().toInt())
            _outputModes[i] = MachineIoMode.byId(buf.readByte().toInt())
            _connectionsCache[i] = buf.readBoolean()
        }
        val numberOfTraits = buf.readVarInt()
        @Suppress("unused")
        for (i in 0..<numberOfTraits) {
            val id = buf.readVarInt()
            traitByNetworkId[id]?.receiveInitialSyncData(buf)
                ?: CLog.error("Could not find MTETrait with id $id at $pos during initial sync")
        }
    }

    override fun writeCustomData(discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        this.holder?.writeCustomData(discriminator, dataWriter)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_FRONT_FACING -> {
                frontFacing = EnumFacing.byIndex(buf.readByte().toInt())
                this.scheduleRenderUpdate()
            }
            UPDATE_INPUT_MODE -> {
                _inputModes[buf.readByte().toInt()] = MachineIoMode.byId(buf.readByte().toInt())
                this.scheduleRenderUpdate()
            }
            UPDATE_OUTPUT_MODE -> {
                _outputModes[buf.readByte().toInt()] = MachineIoMode.byId(buf.readByte().toInt())
                this.scheduleRenderUpdate()
            }
            UPDATE_CONNECTIONS -> {
                _connectionsCache[buf.readByte().toInt()] = buf.readBoolean()
                this.scheduleRenderUpdate()
            }
            SYNC_MTE_TRAIT -> {
                val traitNetworkId = buf.readVarInt()
                val trait = traitByNetworkId[traitNetworkId]
                    ?: run {
                        CLog.error("Could not find MTETrait with id $traitNetworkId at $pos")
                        return
                    }
                trait.receiveCustomData(buf.readVarInt(), buf)
            }
        }
    }

    fun writeMteData(mteTrait: MTETrait, discriminator: Int, dataWriter: PacketBuffer.() -> Unit) {
        writeCustomData(SYNC_MTE_TRAIT) {
            writeVarInt(mteTrait.networkId)
            writeVarInt(discriminator)
            dataWriter()
        }
    }

    open fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.PIPE_CONNECTABLE) {
            return capability.cast(this)
        } else if (capability === ClayiumTileCapabilities.ITEM_FILTER_APPLICATABLE) {
            return capability.cast(filterHolder)
        } else if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemInventory)
            val i = facing.index
            val inputSlots = when (inputModes[i]) {
                FIRST ->  createFilteredItemHandler(RangedItemHandlerProxy(importItems, availableSlot = 0), facing)
                SECOND -> createFilteredItemHandler(RangedItemHandlerProxy(importItems, availableSlot = 1), facing)
                ALL -> createFilteredItemHandler(importItems, facing)
                CE -> this.getCapability(ClayiumTileCapabilities.CLAY_ENERGY_HOLDER, facing)?.energizedClayItemHandler
                else -> null
            }
            val outputSlots = when (outputModes[i]) {
                FIRST ->  createFilteredItemHandler(RangedItemHandlerProxy(exportItems, availableSlot = 0), facing)
                SECOND -> createFilteredItemHandler(RangedItemHandlerProxy(exportItems, availableSlot = 1), facing)
                ALL -> createFilteredItemHandler(exportItems, facing)
                else -> null
            }
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ItemHandlerProxy(inputSlots, outputSlots))
        }
        return mteTraits.values.firstNotNullOfOrNull { it.getCapability(capability, facing) }
    }

    fun <T> hasCapability(capability: Capability<T>, facing: EnumFacing? = null): Boolean {
        return getCapability(capability, facing) != null
    }

    /**
     * this is intended to be used in [getCapability] to create an [IItemHandler] with a filter.
     *
     * @param side if null, it returns original [handler].
     */
    protected fun createFilteredItemHandler(handler: IItemHandler, side: EnumFacing?): IItemHandler {
        if (side == null) return handler
        val filter = filterHolder.getFilter(side)
        return if (filter == null) handler else FilteredItemHandler(handler, filter)
    }

    /**
     * only called on the server side.
     * @return true if something happened i.e. no further processing should be done.
     *
     * For example: if clicked by a tool, maybe you don't want to open the GUI. so return true.
     */
    open fun onRightClickServerSide(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val stack = player.getHeldItem(hand)
        val confTool = stack.getCapability(ClayiumCapabilities.CONFIG_TOOL, null)
        if (confTool != null) {
            this.onToolClick(confTool, player, hand, clickedSide, hitX, hitY, hitZ)
            return true
        }
        val pos = this.pos ?: return false
        if (this.canOpenGui()) {
            MetaTileEntityGuiFactory.open(player, pos)
            return true
        } else {
            return false
        }
    }

    open fun canOpenGui() = true

    open fun onToolClick(toolType: IConfigurationTool.ToolType, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        if (this.world?.isRemote == true) return
        val world = this.world ?: return
        val pos = this.pos ?: return
        when (toolType) {
            PIPING -> {
                world.setBlockState(pos, world.getBlockState(pos).cycleProperty(IS_PIPE))
            }
            INSERTION -> {
                this.toggleInput(clickedSide)
            }
            EXTRACTION -> {
                this.toggleOutput(clickedSide)
            }
            ROTATION -> {
                this.rotate(clickedSide)
                EnumFacing.entries.forEach(this::refreshConnection)
            }
            FILTER_REMOVER -> {
                this.filterHolder.clearFilter(clickedSide)
            }
        }
    }

    fun onToolClick(tool: IConfigurationTool, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val type = tool.getType(player.isSneaking) ?: return false
        this.onToolClick(type, player, hand, clickedSide, hitX, hitY, hitZ)
        return true
    }

    private fun rotate(side: EnumFacing) {
        if (side.axis.isVertical) return
        if (frontFacing == side) {
            frontFacing = frontFacing.opposite
            val oldInputs = _inputModes.toList()
            val oldOutputs = _outputModes.toList()
            for (side in EnumFacing.HORIZONTALS) {
                val rotatedSide = side.opposite
                setInput(rotatedSide, oldInputs[side.index])
                setOutput(rotatedSide, oldOutputs[side.index])
            }
        } else {
            while (frontFacing != side) {
                val oldInputs = _inputModes.toList()
                val oldOutputs = _outputModes.toList()
                frontFacing = frontFacing.rotateY()
                for (side in EnumFacing.HORIZONTALS) {
                    val rotatedSide = side.rotateY()
                    setInput(rotatedSide, oldInputs[side.index])
                    setOutput(rotatedSide, oldOutputs[side.index])
                }
            }
        }
    }

    fun getInput(side: EnumFacing) = _inputModes[side.index]
    fun getOutput(side: EnumFacing) = _outputModes[side.index]

    fun isInputModeValid(mode: MachineIoMode) = mode in validInputModes
    fun isOutputModeValid(mode: MachineIoMode) = mode in validOutputModes

    /**
     * If [mode] is not in [validInputModes], it will not be set.
     */
    fun setInput(side: EnumFacing, mode: MachineIoMode) {
        if (mode !in this.validInputModes) return
        _inputModes[side.index] = mode
        this.refreshConnection(side)
        (this.getNeighborTileEntity(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_INPUT_MODE) {
            writeByte(side.index)
            writeByte(mode.id)
        }
    }

    /**
     * If [mode] is not in [validOutputModes], it will not be set.
     */
    fun setOutput(side: EnumFacing, mode: MachineIoMode) {
        if (mode !in this.validOutputModes) return
        _outputModes[side.index] = mode
        this.refreshConnection(side)
        (this.getNeighborTileEntity(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_OUTPUT_MODE) {
            writeByte(side.index)
            writeByte(mode.id)
        }
    }

    protected fun toggleInput(side: EnumFacing) {
        val current = _inputModes[side.index]
        _inputModes[side.index] = validInputModes[(validInputModes.indexOf(current) + 1) % validInputModes.size]
        this.refreshConnection(side)
        (this.getNeighborTileEntity(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_INPUT_MODE) {
            writeByte(side.index)
            writeByte(_inputModes[side.index].id)
        }
    }

    protected fun toggleOutput(side: EnumFacing) {
        val current = _outputModes[side.index]
        _outputModes[side.index] = validOutputModes[(validOutputModes.indexOf(current) + 1) % validOutputModes.size]
        this.refreshConnection(side)
        (this.getNeighborTileEntity(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_OUTPUT_MODE) {
            writeByte(side.index)
            writeByte(_outputModes[side.index].id)
        }
    }

    protected fun refreshConnection(side: EnumFacing) {
        val previous = _connectionsCache[side.index]
        val i = side.index
        val neighborTileEntity = this.getNeighborTileEntity(side)
        if (neighborTileEntity == null) {
            _connectionsCache[i] = false
        } else {
            val neighborConnectable = neighborTileEntity.getCapability(ClayiumTileCapabilities.PIPE_CONNECTABLE, side.opposite)
            if (neighborConnectable == null) {
                // neighbor has no specific implementation for this logic. default to hasItemHandler.
                _connectionsCache[i] = neighborTileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite)
            } else {
                val thisMode = getPipeConnectionModeForRendering(side)
                val neighborMode = neighborConnectable.getPipeConnectionModeForRendering(side.opposite)
                val neighborConnectionLogic = neighborConnectable.pipeConnectionLogic
                _connectionsCache[i] = (pipeConnectionLogic.canConnect(thisMode = thisMode, neighborMode = neighborMode)
                        || neighborConnectionLogic.canConnect(thisMode = neighborMode, neighborMode = thisMode))
            }
        }
        if (previous != _connectionsCache[i]) {
            writeCustomData(UPDATE_CONNECTIONS) {
                writeByte(i)
                writeBoolean(_connectionsCache[i])
            }
        }
    }

    override fun getPipeConnectionModeForRendering(side: EnumFacing): PipeConnectionMode {
        val input = when (getInput(side)) {
            NONE -> false
            FIRST, SECOND, ALL, CE,
            M_ALL, M_1, M_2, M_3, M_4, M_5, M_6,
            FLUID -> true
        }

        val output = when (getOutput(side)) {
            NONE -> false
            FIRST, SECOND, ALL, CE,
            M_ALL, M_1, M_2, M_3, M_4, M_5, M_6,
            FLUID -> true
        }

        return if (input && output) PipeConnectionMode.BOTH
        else if (input) PipeConnectionMode.INPUT
        else if (output) PipeConnectionMode.OUTPUT
        else PipeConnectionMode.NONE
    }

    /**
     * Called when the machine is destroyed.
     * @param itemBuffer the buffer to add items to be dropped.
     * @see [clearInventory]
     */
    open fun itemsDroppedOnDestroy(itemBuffer: MutableList<ItemStack>) {
        clearInventory(itemBuffer, importItems)
        clearInventory(itemBuffer, exportItems)
    }

    open fun isFacingValid(facing: EnumFacing) = facing.axis.isHorizontal

    @MustBeInvokedByOverriders
    open fun onPlacement() {
        if (!isRemote) EnumFacing.entries.forEach(this::refreshConnection)
        overclockHandler.onNeighborBlockChange()
        mteTraits.values.forEach(MTETrait::onPlacement)
    }

    open fun onRemoval() {
        this.mteTraits.values.forEach(MTETrait::onRemoval)
    }

    fun asStackForm(amount: Int = 1): ItemStack {
        return ItemStack(blockMachine, amount, mteRegistry.getIdByKey(metaTileEntityId))
    }

    /**
     * Called on [Block.onNeighborChange].
     */
    open fun onNeighborChanged(facing: EnumFacing) {}

    /**
     * Called on [Block.neighborChanged].
     */
    open fun neighborChanged() {
        EnumFacing.entries.forEach(this::refreshConnection)
        overclockHandler.onNeighborBlockChange()
    }

    open fun canConnectRedstone(side: EnumFacing?) = false

    open fun getWeakPower(side: EnumFacing?): Int = 0

    fun getNeighborTileEntity(side: EnumFacing) = holder?.getNeighbor(side)
    fun scheduleRenderUpdate() = holder?.scheduleRenderUpdate()
    fun notifyNeighbors() = holder?.notifyNeighbors()

    /**
     * @return null if the neighbor is not loaded.
     */
    fun getNeighborBlockState(side: EnumFacing): IBlockState? {
        val world = this.world ?: return null
        val pos = this.pos?.offset(side) ?: return null

        return if (world.isBlockLoaded(pos)) {
            world.getBlockState(pos)
        } else {
            null
        }
    }

    open fun getItemStackDisplayName(): String {
        return if (SidelessI18n.hasKey("${this.translationKey}.${tier.lowerName}")) {
            SidelessI18n.format("${this.translationKey}.${tier.lowerName}")
        } else {
            SidelessI18n.format(this.translationKey, SidelessI18n.format(this.tier.prefixTranslationKey))
        }
    }

    open fun isInCreativeTab(tab: CreativeTabs): Boolean {
        return tab === CreativeTabs.SEARCH || tab === ClayiumCTabs.main
    }

    open val renderingConfig by lazy {
        MteRenderingConfig.builder().noFrontFacing().build()
    }

    @SideOnly(Side.CLIENT)
    fun hasFilterClient(side: EnumFacing): Boolean {
        return filterHolder.hasFilterClientOnly(side)
    }

    @SideOnly(Side.CLIENT)
    open fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            ModelResourceLocation(ResourceLocation(metaTileEntityId.namespace, "machines/$name"), "tier=${tier.lowerName}"))
    }

    @SideOnly(Side.CLIENT)
    @MustBeInvokedByOverriders
    open fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("tooltip.clayium.tier", tier.numeric))
        UtilLocale.formatTooltips(tooltip, "$translationKey.${tier.lowerName}.tooltip")
        UtilLocale.formatTooltips(tooltip, "$translationKey.tooltip")
    }

    /**
     * Called on init and model reload.
     */
    @SideOnly(Side.CLIENT)
    open fun bakeQuads(getter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {}

    /**
     * Adds base textures such as Machine hulls.
     */
    @SideOnly(Side.CLIENT)
    open fun getQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (state == null || side == null || state !is IExtendedBlockState) return
        quads.add(ModelTextures.getHullQuads(this.tier)?.get(side) ?: return)
    }

    /**
     * Adds overlay textures such as Machine faces.
     * This is called after [getQuads], but before adding IO textures.
     *
     * The reason why don't unify this with [getQuads] is DRY. Consider if you want to add overlay **between** machine hulls and face textures.
     * If unified, you have to write everything (hulls, overlays, face) since `super.getQuads` adds both hulls and face.
     */
    @SideOnly(Side.CLIENT)
    @Suppress("DEPRECATION")
    open fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        val renderingOpts = this.renderingConfig
        val faceTexture = renderingOpts.faceTexture

        val hasFrontFacing = faceTexture != null
        val isThisSideFace = (renderingOpts.useFaceForAllSides || side == this.frontFacing)
        if (hasFrontFacing && isThisSideFace) {
            ModelTextures.FACE_QUADS[faceTexture]?.get(side)?.let { quads.add(it) }
        }
    }

    /**
     * You can use GlStateManager to render extra things if needed.
     * todo: cc render?
     */
    @SideOnly(Side.CLIENT)
    open fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {}

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: PanelSyncManager): ModularPanel {
        return ModularPanel.defaultPanel(translationKey)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    protected inline fun ModularPanel.columnWithPlayerInv(builder: (Flow.() -> Flow)): ModularPanel {
        return this.child(
            Column().margin(7).sizeRel(1f)
                .builder()
                .child(MuiSlots.playerInventory(0))
        )
    }

    /**
     * returns the main parent widget positioned above player inventory.
     */
    protected open fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        return ParentWidget().widthRel(1f).expanded().marginBottom(2)
            .child(IKey.str(asStackForm().displayName).asWidget()
                .align(Alignment.TopLeft))
            .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
            .child(IKey.dynamic {
                // if empty string, a bug occurs.
                if (overclock != 1.0) SidelessI18n.format("gui.clayium.overclock", overclock) else " "
            }.asWidgetResizing().alignment(Alignment.CenterRight).align(Alignment.BottomRight))
    }

    @Deprecated("Use onRightClickServerSide instead.", ReplaceWith("onRightClickServerSide(player, hand, clickedSide, hitX, hitY, hitZ)"))
    @ApiStatus.ScheduledForRemoval(inVersion = "1.0.0.0")
    open fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        return this.onRightClickServerSide(player, hand, clickedSide, hitX, hitY, hitZ)
    }

    @Deprecated("Use asStackForm instead.", ReplaceWith("asStackForm(amount)"))
    @ApiStatus.ScheduledForRemoval(inVersion = "1.0.0.0")
    fun getStackForm(amount: Int = 1) = asStackForm(amount)

    companion object {

        val onlyNoneList = listOf(NONE)
        val energyAndNone = listOf(NONE, CE)
        val bufferValidInputModes = listOf(NONE, ALL)

        const val IMPORT_INVENTORY = "importInventory"
        const val EXPORT_INVENTORY = "exportInventory"

        val validInputModesLists = listOf(
            listOf(NONE, CE),
            listOf(ALL, CE, NONE),
            listOf(ALL, FIRST, SECOND, CE, NONE)
        )

        val validOutputModesLists = listOf(
            onlyNoneList,
            listOf(ALL, NONE),
            listOf(ALL, FIRST, SECOND, NONE)
        )

        /**
         * Clears the inventory and adds all items to the [itemBuffer].
         */
        fun clearInventory(itemBuffer: MutableList<ItemStack>, inventory: IItemHandler) {
            for (i in 0..<inventory.slots) {
                val stack = inventory.getStackInSlot(i)
                if (!stack.isEmpty) {
                    itemBuffer.add(inventory.extractItem(i, Int.MAX_VALUE, false))
                }
            }
        }
    }
}