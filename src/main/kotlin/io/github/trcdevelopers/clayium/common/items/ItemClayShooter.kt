package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.item.ItemTiered
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.common.entities.EntityThrowableClayBullet
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.SoundCategory

abstract class ItemClayShooter(
    maxDamage: Int,
    val bulletLifespanTick: Int,
    val bulletInitialVelocity: Float,
    val bulletDiffusion: Float,
    val bulletDamage: Int,
    val bulletShootingRate: Float,
    val bulletCooldownTick: Int,
    val infinity: Boolean = false,
) : ItemTiered() {

    init {
        this.maxStackSize = 1
    }

    constructor(maxDamage: Int, bulletLifespan: Int, bulletInitialVelocity: Float, bulletDiffusion: Float, bulletDamage: Int, bulletShootingFrame: Int)
            : this(maxDamage, bulletLifespan, bulletInitialVelocity, bulletDiffusion, bulletDamage, 3.0f / bulletShootingFrame, (bulletShootingFrame / 3.0f).toInt())

    override fun getTier(stack: ItemStack): ITier {
        return ClayTiers.ADVANCED // TODO
    }

    @JvmOverloads
    protected fun shoot(stack: ItemStack, player: EntityPlayer, per: Float, critical: Boolean = false) {
        val hurtPitch = 5.0f / (itemRand.nextFloat() * 0.7f + this.bulletDamage.toFloat() * per + 1.0f)
        player.world.playSound(null, player.position, SoundEvents.ENTITY_PLAYER_SMALL_FALL, SoundCategory.PLAYERS, 0.6f, hurtPitch)
        val v = this.bulletInitialVelocity * per
        if (v >= 6f) {
            player.world.playSound(player, player.position, SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 0.01f * (v - 6.0f), 1f)
            player.world.playSound(player, player.position, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.01f * (v - 6.0f), 6.0f / (v + 2.0f) + 0.2f)
        }

        if (!player.world.isRemote) {
            val entityClayBullet = EntityThrowableClayBullet(
                player.world, player, this.bulletLifespanTick, this.bulletInitialVelocity, this.bulletDiffusion, this.bulletDamage, 1, critical
            )
            player.world.spawnEntity(entityClayBullet)
        }

        if (!this.infinity) {
            stack.damageItem(1, player)
        }
    }

}
