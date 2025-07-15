package com.github.trc.clayium.common.blocks.metalchest

import codechicken.lib.render.particle.CustomParticleHandler
import com.cleanroommc.modularui.factory.TileEntityGuiFactory
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.util.BlockMaterial
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getAsItem
import com.github.trc.clayium.common.blocks.BlockMaterialBase
import com.github.trc.clayium.common.blocks.material.BlockCompressed.Companion.MATERIAL_NAME
import com.github.trc.clayium.common.blocks.properties.CMaterialProperty
import com.github.trc.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Suppress("OVERRIDE_DEPRECATION")
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
        val meta = this.getMetaFromState(state)
        val material = mapping[meta] ?: CMaterials.aluminum
        return TileEntityMetalChest(6, 11, 2, material)
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing) =  BlockFaceShape.UNDEFINED

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT
    @SideOnly(Side.CLIENT)
    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.ENTITYBLOCK_ANIMATED

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(getMaterialProperty()).add(MATERIAL_NAME).build()
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val material = getCMaterial(state)
        return (state as IExtendedBlockState).withProperty(MATERIAL_NAME, material.upperCamelName)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true
        TileEntityGuiFactory.INSTANCE.open(playerIn, pos)
        return true
    }

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

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375)
    }

    override fun isFullBlock(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = isFullBlock(state)
    override fun isOpaqueCube(state: IBlockState) = isFullBlock(state)
    override fun causesSuffocation(state: IBlockState) = isFullBlock(state)

    /* BoilerPlate for custom particle handling */
    @SideOnly(Side.CLIENT)
    override fun addHitEffects(state: IBlockState, world: World, target: RayTraceResult, manager: ParticleManager): Boolean {
        CustomParticleHandler.handleHitEffects(state, world, target, manager)
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun addDestroyEffects(world: World, pos: BlockPos, manager: ParticleManager): Boolean {
        CustomParticleHandler.handleDestroyEffects(world, pos, manager)
        return true
    }

    override fun addRunningEffects(state: IBlockState, world: World, pos: BlockPos, entity: Entity): Boolean {
        if (world.isRemote) {
            CustomParticleHandler.handleRunningEffects(world, pos, state, entity)
        }
        return true
    }

    override fun addLandingEffects(state: IBlockState, worldObj: WorldServer, blockPosition: BlockPos, iblockstate: IBlockState, entity: EntityLivingBase, numberOfParticles: Int): Boolean {
        CustomParticleHandler.handleLandingEffects(worldObj, blockPosition, entity, numberOfParticles)
        return true
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