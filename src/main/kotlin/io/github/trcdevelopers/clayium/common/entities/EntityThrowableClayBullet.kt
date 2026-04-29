package io.github.trcdevelopers.clayium.common.entities

import io.github.trcdevelopers.clayium.api.util.CLog
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.Blocks
import net.minecraft.init.SoundEvents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import kotlin.math.floor
import kotlin.math.sqrt

class EntityThrowableClayBullet : EntityThrowable {

    private lateinit var player: EntityPlayer
    private var lifespan: Int = 0
    private var damage: Int = 0
    private var critical: Boolean = false

    private var age = 0

    companion object {
        /**
         * 発射直後にshooter自身に弾が当たるのを防ぐための猶予期間（tick）。
         * EntityThrowableのデフォルト実装でもthrowerを除外しようとするが、
         * 発射直後はエンティティのhitboxがまだ重なっている場合があるため、
         * この期間中はshooterへの着弾判定を明示的に無効化する。
         */
        private const val SHOOTER_GRACE_PERIOD_TICKS = 3
    }

    @Deprecated("Not for direct use.")
    constructor(world: World) : super(world)
    constructor(world: World, player: EntityPlayer, lifespan: Int, damage: Int, critical: Boolean) : super(world, player) {
        this.player = player
        this.lifespan = lifespan
        this.damage = damage
        this.critical = critical
    }

    override fun isInRangeToRenderDist(distance: Double): Boolean {
        var d1 = this.entityBoundingBox.getAverageEdgeLength() * 4.0
        d1 *= 64.0
        return distance < d1 * d1
    }

    override fun hasNoGravity(): Boolean {
        return true
    }

    override fun onUpdate() {
        this.lastTickPosX = this.posX
        this.lastTickPosY = this.posY
        this.lastTickPosZ = this.posZ
        super.onUpdate()
        this.age++
        if (this.age > this.lifespan && !this.world.isRemote) {
            this.setDead()
            return
        }

        if (this.world.isRemote) {
            if (this.isInWater) {
                this.spawnInWaterParticleClient()
            }
            this.spawnFlyingDustParticleClient()
            this.spawnTwinklingParticleClient()
        }
    }

    override fun onImpact(result: RayTraceResult) {
        if (this.isDead || this.world.isRemote) return

        // 発射直後の猶予期間中にshooter自身にヒットした場合は無視する
        val hitEntity = result.entityHit
        if (hitEntity != null && hitEntity == this.thrower && this.age < SHOOTER_GRACE_PERIOD_TICKS) return

        if (result.typeOfHit == RayTraceResult.Type.BLOCK || hitEntity != null) {
            this.posX = result.hitVec.x
            this.posY = result.hitVec.y
            this.posZ = result.hitVec.z
        }

        if (hitEntity != null) {
            this.onEntityHit(hitEntity)
        }

        this.spawnImpactDustParticle()
        this.playImpactSound()

        val s = sqrt((this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) / 100.0)
        if (s >= 0.5f && this.age <= this.lifespan) {
            this.playImpactExplodeSound(s.toFloat())
            this.spawnImpactExplodeParticle()
        }
        this.setDead()
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
        compound.setInteger("lifespan", this.lifespan)
        compound.setInteger("damage", this.damage)
        compound.setBoolean("critical", this.critical)
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
        this.lifespan = compound.getInteger("lifespan")
        this.damage = compound.getInteger("damage")
        this.critical = compound.getBoolean("critical")
    }

    fun onEntityHit(entity: Entity) {
        if (entity is EntityLivingBase && entity.health > 0.0f) {
            if (this.critical) {
                if (!this.world.isRemote) {
                    this.spawnCriticalParticle()
                }
                this.playCriticalSound()
            }
            this.playHitSound()
        }
        if (!this.world.isRemote) {
            this.causeDamage(entity)
        }
    }

    private fun causeDamage(entity: Entity) {
        val mx = entity.motionX
        val my = entity.motionY
        val mz = entity.motionZ
        if (entity != this.thrower) {
            entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.thrower), this.damage.toFloat())
        }

        if (entity is EntityLivingBase) {
            entity.hurtResistantTime = 2 // magic number, why 2?
        }
        entity.motionX = mx
        entity.motionY = my
        entity.motionZ = mz
    }

    private fun playImpactSound() {
        this.world.playSound(null, this.position, SoundEvents.ENTITY_SLIME_HURT, SoundCategory.PLAYERS, 0.06f * this.damage, 1.4f / (this.rand.nextFloat() * 0.4f + 0.6f))
    }

    private fun playImpactExplodeSound(speed: Float) {
        if (speed >= 0.5f && this.age <= this.lifespan) {
            this.world.playSound(null, this.position, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.6f * speed, 0.5f / (this.rand.nextFloat() * 0.4f + 0.6f))
        }
    }

    private fun playHitSound() {
        this.world.playSound(null, this.position, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 0.1f * this.damage, 0.7f)
        this.world.playSound(null, this.position, SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.PLAYERS, 0.05f * this.damage, 0.5f)
        this.world.playSound(null, this.position, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.PLAYERS, 0.2f * this.damage, 1.2f)
        this.world.playSound(null, this.position, SoundEvents.BLOCK_NOTE_HAT, SoundCategory.PLAYERS, 0.3f * this.damage, 1.3f)
        this.world.playSound(null, this.position, SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.PLAYERS, 0.1f * this.damage, 1.2f)
    }

    private fun playCriticalSound() {
        val thrower = this.thrower
        if (thrower !is EntityPlayer) return
        this.world.playSound(thrower, this.thrower.position, SoundEvents.ENTITY_FIREWORK_TWINKLE, SoundCategory.PLAYERS, 10f, 1f)
    }

    private fun spawnCriticalParticle() {
        val worldServer = this.world as? WorldServer ?: return
        worldServer.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, 50, 0.0, -1.0, 0.0, 0.5)
    }

    private fun spawnImpactDustParticle() {
        val worldServer = this.world as? WorldServer ?: return
        repeat(8) {
            worldServer.spawnParticle(
                EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, 1, 0.0, 0.0, 0.0, 0.0, Block.getStateId(Blocks.CLAY.defaultState)
            )
        }
    }

    private fun spawnImpactExplodeParticle() {
        val worldServer = this.world as? WorldServer ?: return
        worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1, 0.0, 0.0, 0.0, 0.0)
    }

    private fun spawnFlyingDustParticleClient() {
        val mx = this.motionX / 20.0
        val my = this.motionY / 20.0
        val mz = this.motionZ / 20.0
        this.world.spawnParticle(
            EnumParticleTypes.BLOCK_DUST, this.lastTickPosX, this.lastTickPosY, this.lastTickPosZ,
            mx, my, mz, Block.getStateId(Blocks.CLAY.defaultState)
        )
        this.world.spawnParticle(
            EnumParticleTypes.BLOCK_DUST, (this.posX + this.lastTickPosX) / 2.0, (this.posY + this.lastTickPosY) / 2.0, (this.posZ + this.lastTickPosZ) / 2.0,
            mx, my, mz, Block.getStateId(Blocks.CLAY.defaultState)
        )
    }

    private fun spawnTwinklingParticleClient() {
        val mx = this.motionX / 10.0
        val my = this.motionY / 10.0
        val mz = this.motionZ / 10.0
        var s = floor(mx * mx + my * my + mz * mz).toInt() / 16
        if (this.age >= this.lifespan) {
            s = 0
        }


        for (i in 0..<s) {

            val m1 = (i - s) / s.toDouble()
            val m2 = s / 2.0

            this.world.spawnParticle(
                EnumParticleTypes.CRIT,
                this.posX + mx * m1,
                this.posY + my * m1,
                this.posZ + mz * m1,
                mx * m2,
                my * m2,
                mz * m2
            )
        }
    }

    private fun spawnInWaterParticleClient() {
        val mx = this.motionX / 10.0
        val my = this.motionY / 10.0
        val mz = this.motionZ / 10.0
        for (i in 0..3) {
            this.world.spawnParticle(
                EnumParticleTypes.WATER_BUBBLE,
                this.posX - mx * 0.25,
                this.posY - my * 0.25,
                this.posZ - mz * 0.25,
                mx,
                my,
                mz,
            )
        }
    }
}