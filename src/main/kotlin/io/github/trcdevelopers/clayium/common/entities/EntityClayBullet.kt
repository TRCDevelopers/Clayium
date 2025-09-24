package io.github.trcdevelopers.clayium.common.entities

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.IProjectile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.SoundEvents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

class EntityClayBullet @Deprecated("Use another constructor, this is for world load.") constructor(
    world: World,
) : Entity(world), IProjectile {

    constructor(world: World, thrower: EntityLivingBase, lifespan: Int, initialVelocity: Float, diffusion: Float, damage: Int, numOfTicks: Int, critical: Boolean)
            : this(world) {
        this.thrower = thrower
        this.lifespan = lifespan
        this.initialVelocity = initialVelocity
        this.diffusion = diffusion
        this.damage = damage
        this.numOfTicks = numOfTicks
        this.critical = critical

        this.setLocationAndAngles(
            thrower.posX, thrower.posY + thrower.eyeHeight.toDouble(), thrower.posZ, thrower.rotationYaw, thrower.rotationPitch
        )

        this.posX -= (cos(this.rotationYaw / 180.0f * Math.PI.toFloat()) * 0.16f)
        this.posY -= 0.1
        this.posZ -= (sin(this.rotationYaw / 180.0f * Math.PI.toFloat()) * 0.16f)
        this.setPosition(this.posX, this.posY, this.posZ)
        val d4 = thrower.posX - (sin(thrower.rotationYaw / 180.0f * Math.PI.toFloat()) * initialVelocity).toDouble() * lifespan.toDouble() - this.posX
        val d5 = thrower.posZ + (cos(thrower.rotationYaw / 180.0f * Math.PI.toFloat()) * initialVelocity).toDouble() * lifespan.toDouble() - this.posZ
        this.rotationYaw = (atan2(d5, d4) * 180.0 / Math.PI).toFloat() - 90.0f
        val f = 0.4f
        this.motionX = (-sin(this.rotationYaw / 180.0f * Math.PI.toFloat()) * cos(this.rotationPitch / 180.0f * Math.PI.toFloat()) * f).toDouble()
        this.motionZ = (cos(this.rotationYaw / 180.0f * Math.PI.toFloat()) * cos(this.rotationPitch / 180.0f * Math.PI.toFloat()) * f).toDouble()
        this.motionY = (-sin((this.rotationPitch) / 180.0f * Math.PI.toFloat()) * f).toDouble()
        this.shoot(this.motionX, this.motionY, this.motionZ, initialVelocity, diffusion)
    }

    private lateinit var thrower: EntityLivingBase
    private var lifespan: Int = 0
    private var initialVelocity: Float = 0f
    private var diffusion: Float = 0f
    private var damage: Int = 0
    private var numOfTicks: Int = 0
    private var critical: Boolean = false

    private val lastTickPos: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()

    private var ticksInGround = 0
    private var ticksInAir = 0
    private var throwableShake = 0

    private var inGround = false

    private val inBlockPos = BlockPos.MutableBlockPos()
    private var inBlock: Block? = null

    private var age = 0

    init {
        this.setSize(0.25f, 0.25f)
    }

    override fun isInRangeToRenderDist(distance: Double): Boolean {
        var d1 = this.entityBoundingBox.getAverageEdgeLength() * 4.0
        d1 *= 64.0
        return distance < d1 * d1
    }

    override fun entityInit() {}

    override fun readEntityFromNBT(compound: NBTTagCompound) {
    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
    }

    override fun shoot(x: Double, y: Double, z: Double, velocity: Float, inaccuracy: Float) {
        val d = sqrt(x * x + y * y + z * z)
        var x1 = x / d
        var y1 = y / d
        var z1 = z / d
        x1 += this.rand.nextGaussian() * 0.0075 * diffusion.toDouble()
        z1 += this.rand.nextGaussian() * 0.0075 * diffusion.toDouble()
        x1 *= velocity.toDouble() / 10.0
        y1 *= velocity.toDouble() / 10.0
        z1 *= velocity.toDouble() / 10.0
        this.motionX = x1
        this.motionY = y1
        this.motionZ = z1
        val d2 = sqrt(x1 * x1 + z1 * z1)
        this.prevRotationYaw = (atan2(x1, z1) * 180.0 / Math.PI).toFloat().also { this.rotationYaw = it }
        this.prevRotationPitch = (atan2(y1, d2) * 180.0 / Math.PI).toFloat().also { this.rotationPitch = it }
        this.ticksInGround = 0
    }

    override fun setVelocity(x: Double, y: Double, z: Double) {
        super.setVelocity(x, y, z)
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            this.rotationYaw = (atan2(motionX, motionZ) * 180.0 / Math.PI).toFloat()
            this.prevRotationYaw = this.rotationYaw
            val d = sqrt(motionX * motionX + motionZ * motionZ)
            this.rotationPitch = (atan2(motionY, d) * 180.0 / Math.PI).toFloat()
            this.prevRotationPitch = this.rotationPitch
        }
    }

    override fun onUpdate() {
        if (this.world.isRemote) {
            this.updatePer()
        } else {
//            repeat(this.numOfTicks) {
                if (!this.isDead) {
                    this.updatePer()
                }
//            }
        }
    }

    private fun updatePer() {
        this.lastTickPos.setPos(this.position)
        super.onUpdate()

        if (this.throwableShake > 0) {
            --this.throwableShake
        }

        if (this.inGround) {
            if (this.world.getBlockState(this.inBlockPos).getBlock() === this.inBlock) {
                ++this.ticksInGround

                if (this.ticksInGround == 1200) {
                    this.setDead()
                }

                return
            }

            this.inGround = false
            this.motionX *= (this.rand.nextFloat() * 0.2f).toDouble()
            this.motionY *= (this.rand.nextFloat() * 0.2f).toDouble()
            this.motionZ *= (this.rand.nextFloat() * 0.2f).toDouble()
            this.ticksInGround = 0
            this.ticksInAir = 0
        } else {
            ++this.ticksInAir
        }


        val current = Vec3d(this.posX, this.posY, this.posZ)
        val after1tickRaw = Vec3d(this.posX + this.motionX * 10, this.posY + this.motionY * 10, this.posZ + this.motionZ * 10)

        val rsBlock = this.world.rayTraceBlocks(current, after1tickRaw)

        val collidedToBlock = rsBlock != null
        val after1tick = if (collidedToBlock) {
             Vec3d(rsBlock.hitVec.x, rsBlock.hitVec.y, rsBlock.hitVec.z)
        } else {
            after1tickRaw
        }

        if (!this.world.isRemote) {
            val entitySearchAabb = this.entityBoundingBox
                .expand(this.motionX * 10, this.motionY * 10, this.motionZ * 10)
                .grow(1.0)
            var collidedEntity: Entity? = null
            var d0 = 0.0
            this.world.getEntitiesWithinAABBExcludingEntity(this, entitySearchAabb).forEach { entity ->
                if (entity.canBeCollidedWith() && (entity != this.thrower || this.ticksInAir >= 5)) {
                    val aabb = entity.entityBoundingBox.grow(0.3)
                    val rs = aabb.calculateIntercept(current, after1tick)
                    if (rs != null) {
                        val distance = current.squareDistanceTo(rs.hitVec)
                        if (distance < d0 || d0 == 0.0) {
                            collidedEntity = entity
                            d0 = distance
                        }
                    }
                }
            }

            val rsEntity = collidedEntity?.let { RayTraceResult(it) }

            val hitEntity = rsEntity != null
            if (hitEntity) {
                this.onImpact(rsEntity)
            } else if (collidedToBlock && this.world.getBlockState(rsBlock.blockPos).block === Blocks.PORTAL) {
                this.setPortal(rsBlock.blockPos)
            }
        }

        this.posX += this.motionX * 10.0
        this.posY += this.motionY * 10.0
        this.posZ += this.motionZ * 10.0

        val d = sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
        this.rotationYaw = (atan2(this.motionX, this.motionZ) * 180.0 / Math.PI).toFloat()

        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f
        }

        var f2: Float = this.getFriction()
        val f3: Float = this.getGravityVelocity()
        if (this.isInWater) {
            this.spawnInWaterParticle()
            f2 = 0.8f
        }

        this.motionX *= f2.toDouble()
        if (this.motionY >= 0.0) {
            this.motionY *= f2.toDouble()
        }

        this.motionZ *= f2.toDouble()
        this.motionY -= f3.toDouble()
        this.setPosition(this.posX, this.posY, this.posZ)
        ++this.age
        if (this.world.isRemote) {
            this.spawnFlyingDustParticle()
            this.spawnTwinklingParticle()
        }
    }

    private fun getFriction(): Float {
        return if (this.age < this.lifespan) 1.0f else 0.6f
    }

    private fun getGravityVelocity(): Float {
        return if (this.age < this.lifespan) 0.0f else 0.45f
    }

    fun onImpact(result: RayTraceResult) {
        if (this.isDead) return

        val hitEntity = result.entityHit
        if (hitEntity != null) {
            this.onEntityHit(hitEntity)
        }

        this.spawnImpactDustParticle()
        this.playImpactSound()

        val s = sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ)
        if (s >= 0.5f && this.age <= this.lifespan) {
            this.playImpactExplodeSound(s.toFloat())
            this.spawnImpactExplodeParticle()
        }
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
            this.world.playSound(null, this.position, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.6f * this.damage, 0.5f / (this.rand.nextFloat() * 0.4f + 0.6f))
        }
    }

    private fun playHitSound() {
        val thrower = this.thrower
        if (thrower !is EntityPlayer) return
        this.world.playSound(thrower, this.thrower.position, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 0.1f * this.damage, 0.7f)
        this.world.playSound(thrower, this.thrower.position, SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.PLAYERS, 0.05f * this.damage, 0.5f)
        this.world.playSound(thrower, this.thrower.position, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.PLAYERS, 0.2f * this.damage, 1.2f)
        this.world.playSound(thrower, this.thrower.position, SoundEvents.BLOCK_NOTE_HAT, SoundCategory.PLAYERS, 0.3f * this.damage, 1.3f)
        this.world.playSound(thrower, this.thrower.position, SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.PLAYERS, 0.1f * this.damage, 1.2f)
    }

    private fun playCriticalSound() {
        val thrower = this.thrower
        if (thrower !is EntityPlayer) return
        this.world.playSound(thrower, this.thrower.position, SoundEvents.ENTITY_FIREWORK_TWINKLE, SoundCategory.PLAYERS, 10f, 1f)
    }

    private fun spawnCriticalParticle() {
        if (world.isRemote) return
        val worldServer = this.world as WorldServer
        worldServer.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, 50, 0.0, -1.0, 0.0, 0.5)
    }

    private fun spawnImpactDustParticle() {
        repeat(8) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, Block.getStateId(Blocks.CLAY.defaultState))
        }
    }

    private fun spawnImpactExplodeParticle() {
        this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0)
    }

    private fun spawnFlyingDustParticle() {
        this.world.spawnParticle(
            EnumParticleTypes.BLOCK_DUST, this.lastTickPosX, this.lastTickPosY, this.lastTickPosZ,
            this.motionX / 2.0, this.motionY / 2.0, this.motionZ / 2.0, Block.getStateId(Blocks.CLAY.defaultState)
        )
        this.world.spawnParticle(
            EnumParticleTypes.BLOCK_DUST, (this.posX + this.lastTickPosX) / 2.0, (this.posY + this.lastTickPosY) / 2.0, (this.posZ + this.lastTickPosZ) / 2.0,
            this.motionX / 2.0, this.motionY / 2.0, this.motionZ / 2.0, Block.getStateId(Blocks.CLAY.defaultState)
        )
    }

    private fun spawnTwinklingParticle() {
        var s = floor(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ).toInt() * 100 / 16
        if (this.age >= this.lifespan) {
            s = 0
        }

        s *= this.numOfTicks * this.numOfTicks

        for (i in 0..<s) {
            this.world.spawnParticle(
                EnumParticleTypes.CRIT,
                this.posX + this.motionX * 10.0 * this.numOfTicks.toDouble() * (i - s).toDouble() / s.toDouble(),
                this.posY + this.motionY * 10.0 * this.numOfTicks.toDouble() * (i - s).toDouble() / s.toDouble(),
                this.posZ + this.motionZ * 10.0 * this.numOfTicks.toDouble() * (i - s).toDouble() / s.toDouble(),
                this.motionX * 10.0 * this.numOfTicks.toDouble() / s.toDouble() / 2.0,
                this.motionY * 10.0 * this.numOfTicks.toDouble() / s.toDouble() / 2.0,
                this.motionZ * 10.0 * this.numOfTicks.toDouble() / s.toDouble() / 2.0
            )
        }
    }

    private fun spawnInWaterParticle() {
        for (i in 0..3) {
            val f4 = 0.25f
            this.world.spawnParticle(
                EnumParticleTypes.WATER_BUBBLE,
                this.posX - this.motionX * f4.toDouble(),
                this.posY - this.motionY * f4.toDouble(),
                this.posZ - this.motionZ * f4.toDouble(),
                this.motionX,
                this.motionY,
                this.motionZ
            )
        }
    }
}