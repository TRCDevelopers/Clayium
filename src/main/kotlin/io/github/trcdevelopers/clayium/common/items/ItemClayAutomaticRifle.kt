package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.capability.ClayiumPlayerData
import io.github.trcdevelopers.clayium.common.util.KeyInput
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP

class ItemClayAutomaticRifle(
    maxDamage: Int,
    bulletLifespan: Int,
    bulletInitialVelocity: Float,
    bulletDiffusion: Float,
    bulletDamage: Int,
    bulletShootingFrame: Int,
) : ItemClayShooter(maxDamage, bulletLifespan, bulletInitialVelocity, bulletDiffusion, bulletDamage, bulletShootingFrame) {

    companion object {
        fun clayShooterTick(player: EntityPlayer) {
            if (player !is EntityPlayerMP) return

            val stack = player.heldItemMainhand
            val shooter = stack.item as? ItemClayAutomaticRifle ?: return
            if (!KeyInput.USE_ITEM.isKeyDown(player)) return

            val data = player.getCapability(ClayiumPlayerData.CAPABILITY, null)
                ?: return

            if (data.clayGunCooldown <= 0) {
                shooter.shoot(stack, player, 1.0f)
                data.clayGunCooldown = shooter.bulletCooldownTick
            }

            if (data.clayGunCooldown > 0) {
                data.clayGunCooldown--
            }
        }
    }
}
