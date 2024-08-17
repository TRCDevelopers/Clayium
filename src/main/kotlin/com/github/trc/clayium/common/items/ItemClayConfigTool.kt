package com.github.trc.clayium.common.items

import com.cleanroommc.modularui.utils.ItemCapabilityProvider
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IConfigurationTool
import com.github.trc.clayium.api.util.getMetaTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

class ItemClayConfigTool(
    maxStackSize: Int = 1,
    maxDamage: Int,
    private val type: IConfigurationTool.ToolType,
    private val typeWhenSneak: IConfigurationTool.ToolType? = null,
) : Item() {
    init {
        this.maxDamage = maxDamage
        this.maxStackSize = maxStackSize
    }

    override fun doesSneakBypassUse(stack: ItemStack, world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        return typeWhenSneak != null
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        val typeToSend = (if (player.isSneaking) typeWhenSneak else type)
            ?: return EnumActionResult.PASS

        val metaTileEntity = world.getMetaTileEntity(pos)
        if (metaTileEntity == null) {
            return EnumActionResult.PASS
        } else {
            metaTileEntity.onToolClick(typeToSend, player, hand, side, hitX, hitY, hitZ)
            return EnumActionResult.SUCCESS
        }
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object : ItemCapabilityProvider {
            override fun <T : Any> getCapability(capability: Capability<T>): T? {
                if (capability === ClayiumCapabilities.CONFIG_TOOL)
                    return ClayiumCapabilities.CONFIG_TOOL.cast(createConfigToolCapability())
                return null
            }
        }
    }

    private fun createConfigToolCapability(): IConfigurationTool {
        return IConfigurationTool { isSneaking ->
            if (isSneaking) typeWhenSneak else type
        }
    }
}