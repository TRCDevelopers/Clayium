package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_FILTER_ITEM
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.capability.impl.VoidingItemHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.model.ModelTextures
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import java.util.function.Function

class VoidContainerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(metaTileEntityId, tier, bufferValidInputModes, onlyNoneList, "void_container") {
    override val requiredTextures: List<ResourceLocation?>
        get() =
            listOf(
                clayiumId("blocks/void_container"),
                clayiumId("blocks/void_container_side"),
                clayiumId("blocks/void_container_top")
            )

    override val faceTexture = clayiumId("blocks/void_container")

    override val importItems: IItemHandlerModifiable = VoidContainerItemHandler()
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = importItems

    private val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)
    private val filterSlot = ClayiumItemStackHandler(this, 1)
    private val filterStack
        get() = filterSlot.getStackInSlot(0)

    private var lastFilterStack: ItemStack = ItemStack.EMPTY

    override fun update() {
        super.update()
        if (isRemote || offsetTimer % 100 != 0L) return
        if (
            filterStack.isEmpty && lastFilterStack.isEmpty ||
                ItemHandlerHelper.canItemStacksStack(lastFilterStack, filterStack)
        )
            return
        lastFilterStack = filterStack.copy()
        writeCustomData(UPDATE_FILTER_ITEM) { writeItemStack(filterStack) }
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return VoidContainerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(
                largeSlot(
                        SyncHandlers.itemSlot(importItems, 0).filter {
                            filterStack.isEmpty ||
                                ItemHandlerHelper.canItemStacksStack(it, filterStack)
                        }
                    )
                    .align(Alignment.Center)
            )
            .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(filterSlot, 0)).right(10).top(15))
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == UPDATE_FILTER_ITEM) {
            filterSlot.setStackInSlot(0, buf.readItemStack())
        } else {
            super.receiveCustomData(discriminator, buf)
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        buf.writeItemStack(filterStack)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        super.receiveInitialSyncData(buf)
        filterSlot.setStackInSlot(0, buf.readItemStack())
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setTag("filterSlot", filterSlot.serializeNBT())
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        filterSlot.deserializeNBT(data.getCompoundTag("filterSlot"))
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(
        getter: Function<ResourceLocation, TextureAtlasSprite>,
        faceBakery: FaceBakery
    ) {
        val sprite = getter.apply(clayiumId("blocks/void_container_side"))
        voidContainerSide = EnumFacing.HORIZONTALS.map { ModelTextures.createQuad(it, sprite) }
        voidContainerTop =
            ModelTextures.createQuad(
                EnumFacing.UP,
                getter.apply(clayiumId("blocks/void_container_top"))
            )
        voidContainerBottom =
            ModelTextures.createQuad(
                EnumFacing.DOWN,
                getter.apply(clayiumId("blocks/void_container_top")),
                uv = floatArrayOf(16f, 16f, 0f, 0f)
            )
    }

    @SideOnly(Side.CLIENT)
    override fun overlayQuads(
        quads: MutableList<BakedQuad>,
        state: IBlockState?,
        side: EnumFacing?,
        rand: Long
    ) {
        super.overlayQuads(quads, state, side, rand)
        if (state == null || side == null || side == this.frontFacing) return
        when (side) {
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.EAST -> quads.add(voidContainerSide[side.horizontalIndex])
            EnumFacing.UP -> quads.add(voidContainerTop)
            EnumFacing.DOWN -> quads.add(voidContainerBottom)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        val stack = filterStack
        if (stack.isEmpty) return

        val mc = Minecraft.getMinecraft()
        GlStateManager.pushMatrix()
        run {
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5)

            when (this.frontFacing) {
                EnumFacing.NORTH -> {}
                EnumFacing.WEST -> GlStateManager.rotate(90f, 0f, 1f, 0f)
                EnumFacing.EAST -> GlStateManager.rotate(270f, 0f, 1f, 0f)
                else -> GlStateManager.rotate(180f, 0f, 1f, 0f)
            }

            GlStateManager.translate(0.0, 0.125, -0.51)
            GlStateManager.scale(0.5f, 0.5f, 0.5f)
            RenderHelper.enableStandardItemLighting()
            mc.renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
            RenderHelper.disableStandardItemLighting()
        }
        GlStateManager.popMatrix()
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(
            item,
            meta,
            ModelResourceLocation(this.metaTileEntityId, "inventory")
        )
    }

    private inner class VoidContainerItemHandler : VoidingItemHandler() {
        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (filterStack.isEmpty || ItemHandlerHelper.canItemStacksStack(stack, filterStack)) {
                return ItemStack.EMPTY
            }
            return stack
        }
    }

    companion object {
        private lateinit var voidContainerSide: List<BakedQuad>
        private lateinit var voidContainerTop: BakedQuad
        private lateinit var voidContainerBottom: BakedQuad
    }
}
