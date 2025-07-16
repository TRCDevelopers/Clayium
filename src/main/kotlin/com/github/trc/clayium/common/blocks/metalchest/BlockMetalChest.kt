package com.github.trc.clayium.common.blocks.metalchest

import com.cleanroommc.modularui.factory.TileEntityGuiFactory
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.util.BlockMaterial
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.blocks.material.BlockMaterialWithDynModel
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
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
        return TileEntityMetalChest(6, 11, 2, material)
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing) =  BlockFaceShape.UNDEFINED

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val te = worldIn.getTileEntity(pos) as? TileEntityMetalChest ?: return
        te.onBlockPlacedBy(placer)
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

    companion object {
        fun create(mapping: Map<Int, CMaterial>): BlockMetalChest {
            val materials = mapping.values
            val prop = CMaterialProperty(materials, "material")
            return object : BlockMetalChest(mapping) {
                override fun getMaterialProperty() = prop
            }
        }
    }
}