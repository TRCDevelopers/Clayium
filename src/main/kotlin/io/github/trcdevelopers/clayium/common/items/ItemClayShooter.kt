package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.capability.ClayiumPlayerData
import io.github.trcdevelopers.clayium.api.item.ItemTiered
import io.github.trcdevelopers.clayium.api.util.ClayTiers
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.common.entities.EntityThrowableClayBullet
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
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

    override fun getMaxItemUseDuration(stack: ItemStack): Int = 72000

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

    private fun shootIfCooledDown(stack: ItemStack, player: EntityPlayer, per: Float) {
        val data = player.getCapability(ClayiumPlayerData.CAPABILITY, null)
            ?: return
        val cooldown = data.clayGunCooldown
        if (cooldown <= 0) {
            this.shoot(player.getHeldItem(player.activeHand), player, per)
            data.clayGunCooldown = this.bulletCooldownTick
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        playerIn.activeHand = handIn
        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun onUsingTick(stack: ItemStack, player: EntityLivingBase, count: Int) {
        if (player !is EntityPlayer) return
        this.shootIfCooledDown(stack, player, 1.0f)
    }
}
