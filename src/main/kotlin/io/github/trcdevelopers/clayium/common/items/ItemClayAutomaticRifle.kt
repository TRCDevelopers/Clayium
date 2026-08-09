package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.capability.ClayiumPlayerData
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

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
            val stack = player.heldItemMainhand
            val shooter = stack.item as? ItemClayAutomaticRifle ?: return

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
