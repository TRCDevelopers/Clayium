package com.github.trc.clayium.common.blocks.metalchest

import com.cleanroommc.modularui.factory.TileEntityGuiFactory
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.util.BlockMaterial
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.blocks.BlockMaterialBase
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

abstract class BlockMetalChest(
    mapping: Map<Int, CMaterial>
) : BlockMaterialBase(BlockMaterial.WOOD, mapping) {

    init {
        setCreativeTab(ClayiumCTabs.decorations)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileEntityMetalChest(6,11,2)
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true
        TileEntityGuiFactory.open(playerIn, pos)
        return true
    }
    @SideOnly(Side.CLIENT)
    override fun registerModels() {
        val loc = ModelResourceLocation(clayiumId("material/compressed_material"), "variant=basic")
        ModelLoader.setCustomStateMapper(this,
            object : StateMapperBase() {
                override fun getModelResourceLocation(state: IBlockState) = loc
            }
        )
        for (state in blockState.validStates) {
            ModelLoader.setCustomModelResourceLocation(this.getAsItem(), this.getMetaFromState(state), loc)
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