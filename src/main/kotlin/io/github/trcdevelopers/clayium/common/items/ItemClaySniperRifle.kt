package io.github.trcdevelopers.clayium.common.items

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

class ItemClaySniperRifle(
    maxDamage: Int,
    bulletLifespan: Int,
    bulletInitialVelocity: Float,
    bulletDiffusion: Float,
    bulletDamage: Int,
    bulletShootingFrame: Int,
    val chargeTimeTick: Int,
) : ItemClayShooter(maxDamage, bulletLifespan, bulletInitialVelocity, bulletDiffusion, bulletDamage, bulletShootingFrame) {

    override fun getItemUseAction(stack: ItemStack): EnumAction = EnumAction.BOW
    override fun getMaxItemUseDuration(stack: ItemStack): Int = 72000

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        playerIn.activeHand = handIn
        return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun onUsingTick(stack: ItemStack, player: EntityLivingBase, count: Int) {
        if (player !is EntityPlayer) return

        val charge = getMaxItemUseDuration(stack) - count
        if (charge == 0) {
            player.world.playSound(player, player.position, SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.5f, 1.3f)
        }
        if (charge == chargeTimeTick) {
            player.world.playSound(player, player.position, SoundEvents.BLOCK_NOTE_HAT, SoundCategory.PLAYERS, 0.5f, 0.6f)
            player.world.playSound(player, player.position, SoundEvents.ENTITY_ENDERDRAGON_HURT, SoundCategory.PLAYERS, 0.2f, 2.5f)
        }
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: EntityLivingBase, timeLeft: Int) {
        val charge = getMaxItemUseDuration(stack) - timeLeft
        if (charge >= chargeTimeTick && entityLiving is EntityPlayer) {
            shoot(stack, entityLiving, per = 1.0f, critical = true)
        }
    }
}
