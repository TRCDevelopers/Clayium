package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.item.ItemTiered
import io.github.trcdevelopers.clayium.api.util.ClayTier
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.api.util.ITier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.world.World
import net.minecraftforge.common.IRarity

open class ItemClayShooter(
    maxDamage: Int,
    protected val bulletLifespanTick: Int,
    protected val bulletInitialVelocity: Float,
    protected val bulletDiffusion: Float,
    protected val bulletDamage: Int,
    protected val bulletShootingRate: Float,
    protected val bulletCooldownTick: Int,
    protected val chargeTimeTick: Int,
    protected val infinity: Boolean = false,
) : ItemTiered() {

    constructor(maxDamage: Int, bulletLifespan: Int, bulletInitialVelocity: Float, bulletDiffusion: Float, bulletDamage: Int, bulletShootingFrame: Int, chargeTime: Int)
            : this(maxDamage, bulletLifespan, bulletInitialVelocity, bulletDiffusion, bulletDamage, 3.0f / bulletShootingFrame, (bulletShootingFrame / 3.0f).toInt(), chargeTime)

    override fun getTier(stack: ItemStack): ITier {
        return ClayTiers.ADVANCED // TODO
    }

    @JvmOverloads
    fun shoot(stack: ItemStack, player: EntityPlayer, per: Float, critical: Boolean = false) {
        val pitch = 5.0f / (itemRand.nextFloat() * 0.7f + bulletDamage.toFloat() * per + 1.0f)
        player.world.playSound(player, player.position, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 0.6f, pitch)

        if (!this.infinity) {
            stack.damageItem(1, player)
        }
    }
}