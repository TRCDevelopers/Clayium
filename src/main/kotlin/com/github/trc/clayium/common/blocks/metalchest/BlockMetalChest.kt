package com.github.trc.clayium.common.blocks.metalchest

import com.cleanroommc.modularui.factory.TileEntityGuiFactory
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.util.BlockMaterial
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.blocks.material.BlockMaterialWithDynModel
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import com.github.trc.clayium.common.config.ConfigMetalChest
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
abstract class BlockMetalChest(
    mapping: Map<Int, CMaterial>
) : BlockMaterialWithDynModel(BlockMaterial.WOOD, mapping) {

    init {
        setCreativeTab(ClayiumCTabs.main)
        setSoundType(SoundType.METAL)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        val meta = this.getMetaFromState(state)
        val material = mapping[meta] ?: CMaterials.aluminum
        val config = metalChestConfig[material.materialId]
            ?: intArrayOf(9, 6, 1)
        val (row, column, pages) = config
        val tileEntity = TileEntityMetalChest()
        tileEntity.init(row, column, pages, material)
        return tileEntity
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing) =  BlockFaceShape.UNDEFINED

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val te = worldIn.getTileEntity(pos) as? TileEntityMetalChest ?: return
        te.onBlockPlacedBy(placer, stack)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true
        TileEntityGuiFactory.INSTANCE.open(playerIn, pos)
        return true
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375)
    }

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    @SideOnly(Side.CLIENT)
    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.ENTITYBLOCK_ANIMATED

    @SideOnly(Side.CLIENT)
    override fun registerModels() {
        val blockLoc = ModelResourceLocation(clayiumId("metal_chest"), "variant=block")
        val itemLoc = ModelResourceLocation(clayiumId("metal_chest"), "inventory")
        ModelLoader.setCustomStateMapper(this,
            object : StateMapperBase() { override fun getModelResourceLocation(state: IBlockState) = blockLoc }
        )
        for (state in blockState.validStates) {
            ModelLoader.setCustomModelResourceLocation(this.getAsItem(), this.getMetaFromState(state), itemLoc)
        }
    }

    override fun eventReceived(state: IBlockState, worldIn: World, pos: BlockPos, id: Int, param: Int): Boolean {
        @Suppress("DEPRECATION")
        return worldIn.getTileEntity(pos)?.receiveClientEvent(id, param) ?: super.eventReceived(state, worldIn, pos, id, param)
    }

    companion object {

        val metalChestConfig = mutableMapOf<ResourceLocation, IntArray>()

        fun create(mapping: Map<Int, CMaterial>): BlockMetalChest {
            val materials = mapping.values
            val prop = CMaterialProperty(materials, "material")
            return object : BlockMetalChest(mapping) {
                override fun getMaterialProperty() = prop
            }
        }

        fun loadMetalChestConfig() {
            for (raw: String in ConfigMetalChest.metalChestConfig) {
                val (rlStr, cfg) = raw.split(";", limit = 2)
                val rl = ResourceLocation(rlStr)
                val (w, h, pages) = cfg.split(",").map { it.toInt() }
                if (w < 1 || h < 1 || pages < 1) {
                    CLog.error("Row, Column, and Pages must be >= 1. Material: $rl")
                    continue
                }
                if (w > 50) {
                    CLog.error("Inventory Width must be <= 50. Material: $rl")
                    continue
                }
                if (h > 20) {
                    CLog.error("Inventory Height be <= 20. Material: $rl")
                    continue
                }
                if (pages > 100) {
                    CLog.error("Inventory Pages must be <= 100. Material: $rl")
                    continue
                }
                metalChestConfig[rl] = intArrayOf(w, h, pages)
            }
        }
    }
}