package io.github.trcdevelopers.clayium.common.blocks.material

import codechicken.lib.block.property.unlisted.UnlistedStringProperty
import codechicken.lib.render.particle.CustomParticleHandler
import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.common.blocks.material.BlockMaterialWithDynModel.Companion.MATERIAL_NAME
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.particle.ParticleManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Mostly same as the [BlockMaterialBase], but with a custom model [io.github.trcdevelopers.clayium.client.model.MaterialBlockBakedModel].
 * Override [registerModels] to register the model path.
 *
 * [MATERIAL_NAME] is added to an extended block state for models.
 */
abstract class BlockMaterialWithDynModel(
    material: net.minecraft.block.material.Material,
    mapping: Map<Int, CMaterial>
) : BlockMaterialBase(material, mapping) {

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this)
            .add(getMaterialProperty()).add(MATERIAL_NAME).build()
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val material = getCMaterial(state)
        return (state as IExtendedBlockState).withProperty(MATERIAL_NAME, material.upperCamelName)
    }

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
        val MATERIAL_NAME = UnlistedStringProperty("material")
    }
}