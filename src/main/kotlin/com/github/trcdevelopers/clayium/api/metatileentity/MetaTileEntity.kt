package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.api.drawable.IDrawable
import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.cleanroommc.modularui.widgets.slot.ModularSlot
import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.block.BlockMachine.Companion.IS_PIPE
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.SYNC_MTE_TRAIT
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_CONNECTIONS
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_FILTER
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_FRONT_FACING
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_INPUT_MODE
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_OUTPUT_MODE
import com.github.trcdevelopers.clayium.api.capability.IItemFilter
import com.github.trcdevelopers.clayium.api.capability.impl.FilteredItemHandler
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.capability.impl.RangedItemHandlerProxy
import com.github.trcdevelopers.clayium.api.gui.MetaTileEntityGuiFactory
import com.github.trcdevelopers.clayium.api.metatileentity.interfaces.ISyncedTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.client.model.ModelTextures
import com.github.trcdevelopers.clayium.common.Clayium
import com.github.trcdevelopers.clayium.common.blocks.IPipeConnectable
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode.*
import com.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool
import com.github.trcdevelopers.clayium.common.items.ItemClayConfigTool.ToolType.*
import com.github.trcdevelopers.clayium.common.items.filter.FilterType
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
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
import net.minecraft.tileentity.TileEntity
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
     * used in item/block name and gui title
     */
    val translationKey: String,
) : ISyncedTileEntity, IGuiHolder<PosGuiData>, IPipeConnectable {

    val forgeRarity = tier.rarity

    var holder: MetaTileEntityHolder? = null
    val world: World? get() = holder?.world
    val pos: BlockPos? get() = holder?.pos
    val isInvalid get() = holder?.isInvalid ?: true
    val isRemote get() = world?.isRemote ?: true

    open val faceTexture: ResourceLocation? = null
    open val requiredTextures get() = listOf(faceTexture)

    protected val mteTraits = mutableMapOf<String, MTETrait>()
    private val traitByNetworkId = Int2ObjectOpenHashMap<MTETrait>()

    abstract val importItems: IItemHandlerModifiable
    abstract val exportItems: IItemHandlerModifiable
    abstract val itemInventory: IItemHandler
    abstract val autoIoHandler: AutoIoHandler
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
            val syncFlag = !(isRemote || field == value)
            if (isFacingValid(value)) field = value
            markDirty()
            if (syncFlag) writeCustomData(UPDATE_FRONT_FACING) { writeByte(value.index) }
        }

    private var timer = 0L
    private val timerOffset = (0..19).random()
    val offsetTimer: Long get() = timer + timerOffset

    /**
     * If true, [faceTexture] will be added to all [EnumFacing].
     */
    open val useFaceForAllSides = false

    @SideOnly(Side.CLIENT)
    abstract fun registerItemModel(item: Item, meta: Int)
    @SideOnly(Side.CLIENT)
    fun registerItemModelDefault(item: Item, meta: Int, name: String) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId(name), "tier=${tier.lowerName}"))
    }

    fun addMetaTileEntityTrait(trait: MTETrait) {
        mteTraits[trait.name] = trait
        traitByNetworkId[trait.networkId] = trait
    }

    fun markDirty() = holder?.markDirty()

    open fun update() {
        if (timer == 0L) {
            onFirstTick()
        }
        mteTraits.values.forEach(MTETrait::update)
        timer++
    }

    open fun onFirstTick() {}

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
        CUtils.writeItems(importItems, "importInventory", data)
        CUtils.writeItems(exportItems, "exportInventory", data)
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
                ?: Clayium.LOGGER.error("Could not find MTETrait with id $id at $pos")
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
                        Clayium.LOGGER.error("Could not find MTETrait with id $traitNetworkId at $pos")
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
        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemInventory)
            val i = facing.index
            val inputSlots = when (inputModes[i]) {
                FIRST ->  createFilteredItemHandler(RangedItemHandlerProxy(importItems, availableSlot = 0), facing)
                SECOND -> createFilteredItemHandler(RangedItemHandlerProxy(importItems, availableSlot = 1), facing)
                ALL -> createFilteredItemHandler(importItems, facing)
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
     */
    open fun onRightClick(player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
        val pos = this.pos ?: return
        if (this.canOpenGui()) {
            MetaTileEntityGuiFactory.open(player, pos)
        }
    }

    open fun canOpenGui() = true

    open fun onToolClick(toolType: ItemClayConfigTool.ToolType, player: EntityPlayer, hand: EnumHand, clickedSide: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
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

    private fun rotate(side: EnumFacing) {
        if (side.axis.isVertical) return
        if (frontFacing == side) {
            frontFacing = frontFacing.opposite
            val oldInputs = _inputModes.toList()
            val oldOutputs = _outputModes.toList()
            for (side in EnumFacing.HORIZONTALS) {
                val rotatedSide = side.opposite
                _inputModes[rotatedSide.index] = oldInputs[side.index]
                _outputModes[rotatedSide.index] = oldOutputs[side.index]
            }
        } else {
            while (frontFacing != side) {
                val oldInputs = _inputModes.toList()
                val oldOutputs = _outputModes.toList()
                frontFacing = frontFacing.rotateY()
                for (side in EnumFacing.HORIZONTALS) {
                    val rotatedSide = side.rotateY()
                    _inputModes[rotatedSide.index] = oldInputs[side.index]
                    _outputModes[rotatedSide.index] = oldOutputs[side.index]
                }
            }
        }
    }

    override fun getInput(side: EnumFacing) = _inputModes[side.index]
    override fun getOutput(side: EnumFacing) = _outputModes[side.index]

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
            writeByte(_inputModes[side.index].id)
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
            writeByte(_outputModes[side.index].id)
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
        when (val neighborTileEntity = this.getNeighbor(side)) {
            is MetaTileEntityHolder -> {
                val neighborMetaTileEntity = neighborTileEntity.metaTileEntity ?: return
                _connectionsCache[i] = (this.canConnectToMte(neighborMetaTileEntity, side) || neighborMetaTileEntity.canConnectToMte(this, side.opposite))
            }
            null -> _connectionsCache[i] = false
            else -> _connectionsCache[i] = this.canConnectTo(neighborTileEntity, side)
        }
        if (previous != _connectionsCache[i]) {
            writeCustomData(UPDATE_CONNECTIONS) {
                writeByte(i)
                writeBoolean(_connectionsCache[i])
            }
        }
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

    protected open fun canConnectToMte(neighbor: MetaTileEntity, side: EnumFacing): Boolean {
        val i = side.index
        val o = side.opposite.index
        return (this._inputModes[i] != NONE && neighbor._outputModes[o] != NONE
                || this._outputModes[i] != NONE && neighbor._inputModes[o] != NONE)
    }

    protected fun canConnectTo(neighbor: TileEntity, side: EnumFacing): Boolean {
        return neighbor.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite)
    }

    open fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        clearInventory(itemBuffer, importItems)
        clearInventory(itemBuffer, exportItems)
    }

    open fun isFacingValid(facing: EnumFacing) = facing.axis.isHorizontal

    @MustBeInvokedByOverriders
    open fun onPlacement() {
        if (!isRemote) EnumFacing.entries.forEach(this::refreshConnection)
    }

    open fun onRemoval() {}

    fun getStackForm(amount: Int = 1): ItemStack {
        return ItemStack(ClayiumApi.BLOCK_MACHINE, amount, ClayiumApi.MTE_REGISTRY.getIdByKey(metaTileEntityId))
    }

    open fun writeItemStackNbt(data: NBTTagCompound) {}
    open fun readItemStackNbt(data: NBTTagCompound) {}

    open fun onNeighborChanged(facing: EnumFacing) {
    }
    open fun neighborChanged() {
        EnumFacing.entries.forEach(this::refreshConnection)
    }

    open fun canConnectRedstone(side: EnumFacing?) = false

    open fun getWeakPower(side: EnumFacing?): Int = 0

    fun getNeighbor(side: EnumFacing) = holder?.getNeighbor(side)
    fun scheduleRenderUpdate() = holder?.scheduleRenderUpdate()

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

    @SideOnly(Side.CLIENT)
    @MustBeInvokedByOverriders
    open fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("tooltip.clayium.tier", tier.numeric))
        UtilLocale.formatTooltips(tooltip, "$translationKey.tooltip")
    }

    open fun isInCreativeTab(tab: CreativeTabs): Boolean {
        return tab === CreativeTabs.SEARCH || tab === Clayium.creativeTab
    }

    open fun shouldRenderInPass(pass: Int) = (pass == 0)
    open fun getMaxRenderDistanceSquared(): Double = 4096.0
    /**
     * null for use TileEntity defaults.
     */
    open val renderBoundingBox: AxisAlignedBB? = null

    @SideOnly(Side.CLIENT)
    open fun bakeQuads(getter: java.util.function.Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {}

    /**
     * Adds base textures such as Machine hulls.
     */
    @SideOnly(Side.CLIENT)
    open fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        if (state == null || side == null || state !is IExtendedBlockState) return mutableListOf()
        val quads = mutableListOf(ModelTextures.getHullQuads(this.tier)?.get(side) ?: return mutableListOf())
        return quads
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

    protected fun largeSlot(slot: ModularSlot) = ParentWidget()
                .size(26, 26)
                .background(ClayGuiTextures.LARGE_SLOT)
                .child(ItemSlot().align(Alignment.Center)
                    .slot(slot)
                    .background(IDrawable.EMPTY))

    private data class FilterAndType(val filter: IItemFilter, val type: FilterType)

    companion object {

        val onlyNoneList = listOf(NONE)
        val energyAndNone = listOf(NONE, CE)
        val bufferValidInputModes = listOf(NONE, ALL)

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

        fun playerInventoryTitle() = IKey.lang("container.inventory").asWidget()
            .debugName("player inventory title")
    }
}