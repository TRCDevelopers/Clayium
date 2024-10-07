package com.github.trc.clayium.api.metatileentity

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.block.BlockMachine.Companion.IS_PIPE
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.INITIALIZE_MTE
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.SYNC_MTE_TRAIT
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_CONNECTIONS
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_FILTER
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_FRONT_FACING
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_INPUT_MODE
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_OUTPUT_MODE
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IConfigurationTool
import com.github.trc.clayium.api.capability.IConfigurationTool.ToolType.*
import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.capability.IPipeConnectable
import com.github.trc.clayium.api.capability.IPipeConnectionLogic
import com.github.trc.clayium.api.capability.PipeConnectionMode
import com.github.trc.clayium.api.capability.impl.FilteredItemHandler
import com.github.trc.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trc.clayium.api.capability.impl.RangedItemHandlerProxy
import com.github.trc.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import com.github.trc.clayium.api.metatileentity.interfaces.IWorldObject
import com.github.trc.clayium.api.metatileentity.trait.OverclockHandler
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.MachineIoMode.*
import com.github.trc.clayium.api.util.asWidgetResizing
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.items.filter.FilterType
import com.github.trc.clayium.common.util.BothSideI18n
import com.github.trc.clayium.common.util.UtilLocale
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
import net.minecraft.util.math.AxisAlignedBB
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
import org.jetbrains.annotations.MustBeInvokedByOverriders
import kotlin.collections.AbstractList

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
) : ISyncedTileEntity, IWorldObject, IGuiHolder<MetaTileEntityGuiData>, IPipeConnectable {

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

    open val faceTexture: ResourceLocation? = null
    open val requiredTextures get() = listOf(faceTexture)

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

    private val filterAndTypes = MutableList<FilterAndType?>(6) { null }
    val filters: List<IItemFilter?> = object : AbstractList<IItemFilter?>() {
        override val size get() = filterAndTypes.size
        override fun get(index: Int) = filterAndTypes[index]?.filter
    }

    open val hasFrontFacing = true
    var frontFacing = EnumFacing.NORTH
        set(value) {
            if (isFacingValid(value)) {
                val syncFlag = !(isRemote || field == value)
                field = value
                markDirty()
                if (syncFlag) writeCustomData(UPDATE_FRONT_FACING) { writeByte(value.index) }
            }
        }

    private var timer = 0L
    private val timerOffset = (0..19).random()
    val offsetTimer: Long get() = timer + timerOffset

    /**
     * If true, [faceTexture] will be added to all [EnumFacing].
     */
    open val useFaceForAllSides = false

    val overclockHandler = OverclockHandler(this)
    val overclock: Double get() = overclockHandler.rawOcFactor

    @SideOnly(Side.CLIENT)
    open fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            ModelResourceLocation(ResourceLocation(metaTileEntityId.namespace, "machines/$name"), "tier=${tier.lowerName}"))
    }

    fun addMetaTileEntityTrait(trait: MTETrait) {
        mteTraits[trait.name] = trait
        traitByNetworkId[trait.networkId] = trait
    }

    override fun markDirty() { holder?.markDirty() }

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

    open fun canBeReplacedTo(world: World, pos: BlockPos, sampleMetaTileEntity: MetaTileEntity): Boolean {
        // shouldn't be replaced if the same MTE.
        if (sampleMetaTileEntity.metaTileEntityId == this.metaTileEntityId) return false
        val thisClass = this::class
        val thatClass = sampleMetaTileEntity::class
        return thisClass == thatClass
    }

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
        markDirty()
        Block.spawnAsEntity(world, pos, this.getStackForm())
        this.onReplace(world, pos, newMetaTileEntity, data)
        this.scheduleRenderUpdate()
    }

    protected open fun onReplace(world: World, pos: BlockPos, newMetaTileEntity: MetaTileEntity, oldMteData: NBTTagCompound) {}

    open fun writeToNBT(data: NBTTagCompound) {
        data.setByte("frontFacing", frontFacing.index.toByte())
        data.setByteArray("inputModes", ByteArray(6) { _inputModes[it].id.toByte() })
        data.setByteArray("outputModes", ByteArray(6) { _outputModes[it].id.toByte() })
        data.setByteArray("connections", ByteArray(6) { if (_connectionsCache[it]) 1 else 0 })
        filterAndTypes.forEachIndexed { i, filterAndType ->
            if (filterAndType == null) return@forEachIndexed
            data.setInteger("filterType$i", filterAndType.type.id)
            data.setTag("filter$i", filterAndType.filter.serializeNBT())
        }
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
        filterAndTypes.forEachIndexed { i, filter ->
            if (data.hasKey("filterType$i") && data.hasKey("filter$i")) {
                val type = FilterType.byId(data.getInteger("filterType$i"))
                val filter = type.factory()
                filter.deserializeNBT(data.getCompoundTag("filter$i"))
                filterAndTypes[i] = FilterAndType(filter, type)
            }
        }
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
            buf.writeVarInt(filterAndTypes[i]?.type?.id ?: -1)
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
            val typeId = buf.readVarInt()
            if (typeId != -1) {
                val filterType = FilterType.byId(typeId)
                this.setFilter(EnumFacing.byIndex(i), filterType.factory(), filterType)
            }
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
            UPDATE_FILTER -> {
                val side = buf.readVarInt()
                val typeId = buf.readVarInt()
                if (typeId == -1) {
                    filterAndTypes[side] = null
                } else {
                    val type = FilterType.byId(typeId)
                    // on the client side, the filter is only used to rendering, so we don't have to deserialize it.
                    filterAndTypes[side] = FilterAndType(type.factory(), type)
                }
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
        }
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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
        val filter = filters[side.index]
        return if (filter == null) handler else FilteredItemHandler(handler, filter)
    }

    /**
     * only called on the server side.
     * @return true if something happened and no further processing should be done.
     */
    open fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
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
                this.removeFilter(clickedSide)
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
        (this.getNeighbor(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
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
        (this.getNeighbor(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_OUTPUT_MODE) {
            writeByte(side.index)
            writeByte(mode.id)
        }
    }

    protected fun toggleInput(side: EnumFacing) {
        val current = _inputModes[side.index]
        _inputModes[side.index] = validInputModes[(validInputModes.indexOf(current) + 1) % validInputModes.size]
        this.refreshConnection(side)
        (this.getNeighbor(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_INPUT_MODE) {
            writeByte(side.index)
            writeByte(_inputModes[side.index].id)
        }
    }

    protected fun toggleOutput(side: EnumFacing) {
        val current = _outputModes[side.index]
        _outputModes[side.index] = validOutputModes[(validOutputModes.indexOf(current) + 1) % validOutputModes.size]
        this.refreshConnection(side)
        (this.getNeighbor(side) as? MetaTileEntityHolder)?.metaTileEntity?.refreshConnection(side.opposite)
        writeCustomData(UPDATE_OUTPUT_MODE) {
            writeByte(side.index)
            writeByte(_outputModes[side.index].id)
        }
    }

    protected fun refreshConnection(side: EnumFacing) {
        val previous = _connectionsCache[side.index]
        val i = side.index
        val neighborTileEntity = this.getNeighbor(side)
        if (neighborTileEntity == null) {
            _connectionsCache[i] = false
        } else {
            val neighborConnectable = neighborTileEntity.getCapability(ClayiumTileCapabilities.PIPE_CONNECTABLE, side.opposite)
            if (neighborConnectable == null) {
                // neighbor has no specific implementation for this logic. default to hasItemHandler.
                _connectionsCache[i] = neighborTileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite)
            } else {
                val thisMode = getPipeConnectionMode(side)
                val neighborMode = neighborConnectable.getPipeConnectionMode(side.opposite)
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

    override fun getPipeConnectionMode(side: EnumFacing): PipeConnectionMode {
        val input = when (getInput(side)) {
            NONE -> false
            FIRST, SECOND, ALL, CE,
            M_ALL, M_1, M_2, M_3, M_4, M_5, M_6 -> true
        }

        val output = when (getOutput(side)) {
            NONE -> false
            FIRST, SECOND, ALL, CE,
            M_ALL, M_1, M_2, M_3, M_4, M_5, M_6 -> true
        }

        return if (input && output) PipeConnectionMode.BOTH
        else if (input) PipeConnectionMode.INPUT
        else if (output) PipeConnectionMode.OUTPUT
        else PipeConnectionMode.NONE
    }

    fun setFilter(side: EnumFacing, filter: IItemFilter, type: FilterType) {
        filterAndTypes[side.index] = FilterAndType(filter, type)
        writeCustomData(UPDATE_FILTER) {
            writeVarInt(side.index)
            writeVarInt(type.id)
        }
    }

    fun removeFilter(side: EnumFacing) {
        filterAndTypes[side.index] = null
        writeCustomData(UPDATE_FILTER) {
            writeVarInt(side.index)
            writeVarInt(-1)
        }
    }

    open fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
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

    fun getStackForm(amount: Int = 1): ItemStack {
        return ItemStack(blockMachine, amount, mteRegistry.getIdByKey(metaTileEntityId))
    }

    open fun writeItemStackNbt(data: NBTTagCompound) {}
    open fun readItemStackNbt(data: NBTTagCompound) {}

    open fun onNeighborChanged(facing: EnumFacing) {
    }
    open fun neighborChanged() {
        EnumFacing.entries.forEach(this::refreshConnection)
        overclockHandler.onNeighborBlockChange()
    }

    open fun canConnectRedstone(side: EnumFacing?) = false

    open fun getWeakPower(side: EnumFacing?): Int = 0

    fun getNeighbor(side: EnumFacing) = holder?.getNeighbor(side)
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
        return if (BothSideI18n.hasKey("${this.translationKey}.${tier.lowerName}")) {
            BothSideI18n.format("${this.translationKey}.${tier.lowerName}")
        } else {
            BothSideI18n.format(this.translationKey, BothSideI18n.format(this.tier.prefixTranslationKey))
        }
    }

    @SideOnly(Side.CLIENT)
    @MustBeInvokedByOverriders
    open fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("tooltip.clayium.tier", tier.numeric))
        UtilLocale.formatTooltips(tooltip, "$translationKey.${tier.lowerName}.tooltip")
        UtilLocale.formatTooltips(tooltip, "$translationKey.tooltip")
    }

    open fun isInCreativeTab(tab: CreativeTabs): Boolean {
        return tab === CreativeTabs.SEARCH || tab === ClayiumCTabs.main
    }

    @SideOnly(Side.CLIENT)
    open fun shouldRenderInPass(pass: Int) = (pass == 0)
    @SideOnly(Side.CLIENT)
    open fun getMaxRenderDistanceSquared(): Double = 4096.0
    /**
     * null for use TileEntity defaults.
     */
    @SideOnly(Side.CLIENT)
    open fun getRenderBoundingBox(): AxisAlignedBB? = null

    /**
     * also called on model reload.
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
     */
    @SideOnly(Side.CLIENT)
    open fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        if (this.hasFrontFacing && this.faceTexture != null) {
            if (this.useFaceForAllSides || side == this.frontFacing) {
                ModelTextures.FACE_QUADS[this.faceTexture]?.get(side)?.let { quads.add(it) }
            }
        }
    }

    /**
     * You can use GlStateManager to render extra things if needed.
     * todo: cc render?
     */
    @SideOnly(Side.CLIENT)
    open fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {}
    @SideOnly(Side.CLIENT)
    open fun useGlobalRenderer() = false

    protected fun largeSlot(slot: ModularSlot) = ParentWidget()
                .size(26, 26)
                .background(ClayGuiTextures.LARGE_SLOT)
                .child(ItemSlot().align(Alignment.Center)
                    .slot(slot)
                    .background(IDrawable.EMPTY))

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel(translationKey)
            .columnWithPlayerInv {
                child(buildMainParentWidget(syncManager))
            }
    }

    protected inline fun ModularPanel.columnWithPlayerInv(builder: (Column.() -> Column)) = this.child(
        Column().margin(7).sizeRel(1f)
            .builder()
            .child(SlotGroupWidget.playerInventory(0))
    )

    /**
     * returns the main parent widget positioned above player inventory.
     */
    protected open fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return ParentWidget().widthRel(1f).expanded().marginBottom(2)
            .child(IKey.str(getStackForm().displayName).asWidget()
                .align(Alignment.TopLeft))
            .child(IKey.lang("container.inventory").asWidget().align(Alignment.BottomLeft))
            .child(IKey.dynamic {
                // if empty string, a bug occurs.
                if (overclock != 1.0) I18n.format("gui.clayium.overclock", overclock) else " "
            }.asWidgetResizing().alignment(Alignment.CenterRight).align(Alignment.BottomRight))
    }

    private data class FilterAndType(val filter: IItemFilter, val type: FilterType)

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

        fun clearInventory(itemBuffer: MutableList<ItemStack>, inventory: IItemHandlerModifiable) {
            for (i in 0..<inventory.slots) {
                val stack = inventory.getStackInSlot(i)
                if (!stack.isEmpty) {
                    itemBuffer.add(stack)
                    inventory.setStackInSlot(i, ItemStack.EMPTY)
                }
            }
        }
    }
}