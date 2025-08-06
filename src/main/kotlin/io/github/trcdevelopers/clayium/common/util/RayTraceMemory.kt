package io.github.trcdevelopers.clayium.common.util

import io.github.trcdevelopers.clayium.api.util.CUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sqrt

class RayTraceMemory(
    val entityEyePosAbsolute: Vec3d,
    val hitPositionAbsolute: Vec3d,
    val side: EnumFacing,
) {

    val entityRelPos = Vec3d(
        entityEyePosAbsolute.x - MathHelper.floor(entityEyePosAbsolute.x),
        entityEyePosAbsolute.y - MathHelper.floor(entityEyePosAbsolute.y),
        entityEyePosAbsolute.z - MathHelper.floor(entityEyePosAbsolute.z),
    )

    val hitRelativeFromEntity = Vec3d(
        hitPositionAbsolute.x - entityEyePosAbsolute.x,
        hitPositionAbsolute.y - entityEyePosAbsolute.y,
        hitPositionAbsolute.z - entityEyePosAbsolute.z,
    )

    /**
     * A hit position of the block relative from a (0, 0, 0) of the block. (bottom-left corner of block's south face).
     *
     * If you click a center of the block's south face, then it will be (0.5, 0.5, 0.0).
     */
    val hit = Vec3d(
        hitPositionAbsolute.x - MathHelper.floor(hitPositionAbsolute.x),
        hitPositionAbsolute.y - MathHelper.floor(hitPositionAbsolute.y),
        hitPositionAbsolute.z - MathHelper.floor(hitPositionAbsolute.z),
    )

    /**
     * Player to hit position vector. Relative to the player position.
     */
    val look: Vec3d = entityRelPos.add(hitRelativeFromEntity.subtract(hit))
    val normalizedLook: Vec3d = look.normalize()

    val yaw = Math.toDegrees(atan2(-look.x, look.z))
    val pitch = Math.toDegrees(atan2(-look.y, sqrt(look.x * look.x + look.z * look.z)))

    val reach = max(look.length(), 3.0)
    val reachVec = Vec3d(
        normalizedLook.x * reach,
        normalizedLook.y * reach,
        normalizedLook.z * reach
    )

    fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()
        nbt.setDouble("ex", entityEyePosAbsolute.x)
        nbt.setDouble("ey", entityEyePosAbsolute.y)
        nbt.setDouble("ez", entityEyePosAbsolute.z)
        nbt.setDouble("hx", hitPositionAbsolute.x)
        nbt.setDouble("hy", hitPositionAbsolute.y)
        nbt.setDouble("hz", hitPositionAbsolute.z)
        nbt.setInteger("side", side.index)
        return nbt
    }

    fun interactBlock(stack: ItemStack, world: WorldServer, pos: BlockPos, sneak: Boolean): InventoryPlayer {
        val player = CUtils.getFakeSurvivalPlayerWithItem(world, stack)
        player.world = world
        player.setPositionAndRotation(
            entityRelPos.x + pos.x.toDouble(), entityRelPos.y + pos.y.toDouble(), entityRelPos.z + pos.z.toDouble(),
            yaw.toFloat(), pitch.toFloat()
        )
        player.isSneaking = sneak
        player.interactionManager.processRightClickBlock(
            player, world, stack, EnumHand.MAIN_HAND, pos, this.side,
            hit.x.toFloat(), hit.y.toFloat(), hit.z.toFloat()
        )
        return player.inventory
    }

    fun rayTraceBlockFrom(world: World, pos: BlockPos, stopOnLiquid: Boolean, ignoreBlockWithoutBoundingBox: Boolean, returnLastUncollidableBlock: Boolean): RayTraceResult? {
        val start = this.entityRelPos.add(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        val end = start.add(reachVec)

        return world.rayTraceBlocks(start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock)
    }

    fun rayTraceEntityFrom(world: World, pos: BlockPos): RayTraceResult? {
        val start = this.entityRelPos.add(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        val end = start.add(reachVec)
        val aabb = AxisAlignedBB(start, end).expand(1.0, 1.0, 1.0)
        val entities = world.getEntitiesWithinAABB(Entity::class.java, aabb) { it != null && it.canBeCollidedWith() }
        var pointedEntity: Pair<Entity, Vec3d>? = null
        var nearestEntityDistance = reach

        for (entity in entities) {
            val collisionBorderSize = entity.collisionBorderSize.toDouble()
            val aabb = entity.entityBoundingBox.expand(collisionBorderSize, collisionBorderSize, collisionBorderSize)
            val rayTraceResult = aabb.calculateIntercept(start, end)

            val entityAabbContainsRayStartPosition = aabb.contains(start)
            if (entityAabbContainsRayStartPosition) {
                if (0.0 < nearestEntityDistance || nearestEntityDistance == 0.0) {
                    return RayTraceResult(entity, rayTraceResult?.hitVec ?: start)
                }
            } else if (rayTraceResult != null) {
                val distance = start.distanceTo(rayTraceResult.hitVec)
                if (distance < nearestEntityDistance || nearestEntityDistance == 0.0) {
                    nearestEntityDistance = distance
                    pointedEntity = Pair(entity, rayTraceResult.hitVec)
                }
            }
        }
        if (pointedEntity != null && nearestEntityDistance < reach) {
            return RayTraceResult(pointedEntity.first, pointedEntity.second)
        }
        return null
    }

    override fun toString(): String {
        return "RayTraceMemory(entityEyePosAbsolute=$entityEyePosAbsolute, hitPositionAbsolute=$hitPositionAbsolute, side=$side, entityRelPos=$entityRelPos, hitPosRelFromEntity=$hitRelativeFromEntity, hit=$hit, look=$look, normalizedLook=$normalizedLook, yaw=$yaw, pitch=$pitch, reach=$reach, reachVec=$reachVec)"
    }

    companion object {

        private val standardMemories = EnumFacing.entries.map { facing ->
            val reach = 3.0
            val entity = Vec3d(getBoundary(-facing.xOffset, 0.999), getBoundary(-facing.yOffset, 0.999), getBoundary(-facing.zOffset, 0.999))
            val hit = Vec3d(getBoundary(-facing.xOffset, 1.0), getBoundary(-facing.yOffset, 1.0), getBoundary(-facing.zOffset, 1.0))
                .add(facing.xOffset * reach, facing.yOffset * reach, facing.zOffset * reach)
            RayTraceMemory(entity, hit, facing.opposite) // hit side is opposite of raytrace direction
        }

        fun from(nbt: NBTTagCompound?): RayTraceMemory? {
            if (nbt == null) return null
            val entityPos = Vec3d(
                nbt.getDouble("ex"),
                nbt.getDouble("ey"),
                nbt.getDouble("ez")
            )
            val hitPos = Vec3d(
                nbt.getDouble("hx"),
                nbt.getDouble("hy"),
                nbt.getDouble("hz")
            )
            val side = EnumFacing.byIndex(nbt.getInteger("side"))
            return RayTraceMemory(entityPos, hitPos, side)
        }

        fun getByFacing(facing: EnumFacing): RayTraceMemory {
            return standardMemories[facing.index]
        }

        private fun getBoundary(i: Int, d: Double): Double {
            return when (i) {
                0 -> 0.5
                1 -> d
                else -> 1.0 - d
            }
        }
    }
}