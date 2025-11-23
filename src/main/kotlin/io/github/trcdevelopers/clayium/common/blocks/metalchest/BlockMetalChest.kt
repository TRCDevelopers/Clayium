package io.github.trcdevelopers.clayium.common.blocks.metalchest

import codechicken.lib.render.particle.CustomParticleHandler
import com.cleanroommc.modularui.factory.TileEntityGuiFactory
import io.github.trcdevelopers.clayium.api.ClayiumApi
import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.util.BlockMaterial
import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.api.util.getAsItem
import io.github.trcdevelopers.clayium.common.blocks.material.BlockMaterialWithDynModel
import io.github.trcdevelopers.clayium.common.config.ConfigMetalChest
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
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
class BlockMetalChest : Block(BlockMaterial.IRON) {

    init {
        setCreativeTab(ClayiumCTabs.main)
        setSoundType(SoundType.METAL)
        setHardness(2.0f)
        setResistance(2.0f)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(BlockMaterialWithDynModel.MATERIAL_NAME).build()
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val te = world.getTileEntity(pos) as? TileEntityMetalChest ?: return state
        return (super.getExtendedState(state, world, pos) as IExtendedBlockState)
            .withProperty(BlockMaterialWithDynModel.MATERIAL_NAME, te.material.upperCamelName)
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        // NOTE: JEI calls getSubBlocks. So, even if you override ItemBlock.getSubItems, you must also override Block.getSubBlocks.
        // ItemBlock.getSubItems calls Block.getSubBlocks, so simply overring Block.getSubBlocks is enough.
        for (material in ClayiumApi.materialRegistry) {
            if (metalChestConfig[material.materialId] != null) {
                items.add(ItemStack(this, 1, material.metaItemSubId))
            }
        }
    }

    override fun hasTileEntity(state: IBlockState) = true

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        val tileEntity = TileEntityMetalChest()
        return tileEntity
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing) =  BlockFaceShape.UNDEFINED

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val te = worldIn.getTileEntity(pos) as? TileEntityMetalChest ?: return
        val meta = stack.metadata
        val material = ClayiumApi.materialRegistry.getObjectById(meta) ?: CMaterials.aluminum
        te.init(material)
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

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        val config = metalChestConfig[this.getCMaterial(stack).materialId]
            ?: return
        val (row, column, pages) = config
        if (pages == 1) {
            tooltip.add(I18n.format("tile.clayium.metal_chest.tooltip_sp", row, column, row * column))
        } else {
            tooltip.add(I18n.format("tile.clayium.metal_chest.tooltip_mp", row, column, pages, row * column * pages))
        }
    }

    fun getCMaterial(stack: ItemStack): CMaterial {
        val meta = stack.metadata
        return ClayiumApi.materialRegistry.getObjectById(meta) ?: CMaterials.aluminum
    }

    override fun eventReceived(state: IBlockState, worldIn: World, pos: BlockPos, id: Int, param: Int): Boolean {
        @Suppress("DEPRECATION")
        return worldIn.getTileEntity(pos)?.receiveClientEvent(id, param) ?: super.eventReceived(state, worldIn, pos, id, param)
    }

    private val beingBrokenMeta = ThreadLocal<Int>()

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val te = worldIn.getTileEntity(pos) as? TileEntityMetalChest
        if (te != null) {
            for (stack in te.itemDroppedOnDestroy()) {
                spawnAsEntity(worldIn, pos, stack)
            }
            beingBrokenMeta.set(te.material.metaItemSubId)
        }
        super.breakBlock(worldIn, pos, state)
    }

    override fun harvestBlock(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
        if (te as? TileEntityMetalChest != null) beingBrokenMeta.set(te.material.metaItemSubId)
        super.harvestBlock(worldIn, player, pos, state, te, stack)
        beingBrokenMeta.remove()
    }

    override fun getDrops(drops: NonNullList<ItemStack?>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        val te = world.getTileEntity(pos) as? TileEntityMetalChest
        if (te != null) {
            drops.add(ItemStack(this, 1, te.material.metaItemSubId))
        } else {
            val meta = beingBrokenMeta.get() ?: 0 // Use default value if beingBrokenMeta.get() is null
            drops.add(ItemStack(this, 1, meta))
        }
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
        val te = world.getTileEntity(pos) as? TileEntityMetalChest
        return if (te != null) {
            ItemStack(this, 1, te.material.metaItemSubId)
        } else {
            ItemStack(this, 1, 0)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderType(state: IBlockState) = EnumBlockRenderType.ENTITYBLOCK_ANIMATED

    @SideOnly(Side.CLIENT)
    fun registerModels() {
        val itemLoc = ModelResourceLocation(clayiumId("metal_chest"), "inventory")
        for (material in ClayiumApi.materialRegistry) {
            ModelLoader.setCustomModelResourceLocation(this.getAsItem(), material.metaItemSubId, itemLoc)
        }
    }

    override fun hasCustomBreakingProgress(state: IBlockState): Boolean {
        return true
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

        val metalChestConfig = mutableMapOf<ResourceLocation, IntArray>()

        private const val MAX_WIDTH = 50
        private const val MAX_HEIGHT = 20
        private const val MAX_PAGES = 100

        fun loadMetalChestConfig() {
            for (raw: String in ConfigMetalChest.metalChestConfig) {
                val (rlStr, cfg) = raw.split(";", limit = 2)
                val rl = ResourceLocation(rlStr)
                val (w, h, pages) = cfg.split(",").map { it.toInt() }
                if (w < 1 || h < 1 || pages < 1) {
                    CLog.error("Row, Column, and Pages must be >= 1. Material: $rl")
                    continue
                }
                if (w > MAX_WIDTH) {
                    CLog.error("Inventory Width must be <= 50. Material: $rl")
                    continue
                }
                if (h > MAX_HEIGHT) {
                    CLog.error("Inventory Height be <= 20. Material: $rl")
                    continue
                }
                if (pages > MAX_PAGES) {
                    CLog.error("Inventory Pages must be <= 100. Material: $rl")
                    continue
                }
                metalChestConfig[rl] = intArrayOf(w, h, pages)
            }
        }
    }
}