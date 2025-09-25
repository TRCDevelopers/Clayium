package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.item.ItemTiered
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.common.entities.EntityThrowableClayBullet
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.world.World

open class ItemClayShooter(
    maxDamage: Int,
    val bulletLifespanTick: Int,
    val bulletInitialVelocity: Float,
    val bulletDiffusion: Float,
    val bulletDamage: Int,
    val bulletShootingRate: Float,
    val bulletCooldownTick: Int,
    val chargeTimeTick: Int,
    val infinity: Boolean = false,
) : ItemTiered() {

    init {
        this.maxStackSize = 1
    }

    val isCharger = chargeTimeTick > 0

    constructor(maxDamage: Int, bulletLifespan: Int, bulletInitialVelocity: Float, bulletDiffusion: Float, bulletDamage: Int, bulletShootingFrame: Int, chargeTime: Int)
            : this(maxDamage, bulletLifespan, bulletInitialVelocity, bulletDiffusion, bulletDamage, 3.0f / bulletShootingFrame, (bulletShootingFrame / 3.0f).toInt(), chargeTime)

    override fun getTier(stack: ItemStack): ITier {
        return ClayTiers.ADVANCED // TODO
    }

    @JvmOverloads
    fun shoot(stack: ItemStack, player: EntityPlayer, per: Float, critical: Boolean = false) {
        val hurtPitch = 5.0f / (itemRand.nextFloat() * 0.7f + this.bulletDamage.toFloat() * per + 1.0f)
        player.world.playSound(player, player.position, SoundEvents.ENTITY_PLAYER_SMALL_FALL, SoundCategory.PLAYERS, 0.6f, hurtPitch)
        val v = this.bulletInitialVelocity * per
        if (v >= 6f) {
            player.world.playSound(player, player.position, SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 0.01f * (v - 6.0f), 1f)
            player.world.playSound(player, player.position, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.01f * (v - 6.0f), 6.0f / (v + 2.0f) + 0.2f)
        }

        if (!player.world.isRemote) {
//            val entityClayBullet = EntityClayBullet(
//                    player.world, player, this.bulletLifespanTick, this.bulletInitialVelocity * per, this.bulletDiffusion,
//                    (this.bulletDamage * per).toInt(), 1, critical
//            )
            val entityClayBullet = EntityThrowableClayBullet(
                player.world, player, this.bulletLifespanTick, this.bulletDamage, critical
            )
            entityClayBullet.shoot(player, player.rotationPitch, player.rotationYaw, 0f, this.bulletInitialVelocity * per, this.bulletDiffusion)
            player.world.spawnEntity(entityClayBullet)
        }

        if (!this.infinity) {
            stack.damageItem(1, player)
        }
    }

    override fun getItemUseAction(stack: ItemStack): EnumAction {
        return EnumAction.BOW
    }

    override fun getMaxItemUseDuration(stack: ItemStack): Int {
        // 72000 used in ItemBow and ItemShield
        return 72000
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        if (this.isCharger) {
            playerIn.activeHand = handIn
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun onUsingTick(stack: ItemStack, player: EntityLivingBase, count: Int) {
        if (player !is EntityPlayer) return
        if (this.isCharger) {
            val c = this.getMaxItemUseDuration(stack) - count
            if (c == 0) {
                player.world.playSound(player, player.position, SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.5f, 1.3f)
            }

            if (c == this.chargeTimeTick) {
                player.world.playSound(player, player.position, SoundEvents.BLOCK_NOTE_HAT, SoundCategory.PLAYERS, 0.5f, 0.6f)
                player.world.playSound(player, player.position, SoundEvents.ENTITY_ENDERDRAGON_HURT, SoundCategory.PLAYERS, 0.2f, 2.5f)
            }
        }
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        if (this.isCharger) {
            val charge = this.getMaxItemUseDuration(stack) - timeLeft
            if (charge >= this.chargeTimeTick && entityLiving is EntityPlayer) {
                this.shoot(stack, entityLiving, 1.0f)
            }
        }
    }
}