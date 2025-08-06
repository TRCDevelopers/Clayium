package io.github.trcdevelopers.clayium.common.items.filter

import com.cleanroommc.modularui.factory.HandGuiData
import com.cleanroommc.modularui.factory.ItemGuiFactory
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import io.github.trcdevelopers.clayium.api.capability.ItemCapabilityProvider
import io.github.trcdevelopers.clayium.common.util.UtilLocale
import io.github.trcdevelopers.clayium.integration.modularui.IGuiHolderClayium
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Supplier

abstract class ItemFilterBase(
    val emptyFilterSupplier: Supplier<IItemFilter>,
) : Item(), IGuiHolderClayium<HandGuiData> {

    abstract fun createItemFilter(stack: ItemStack): IItemFilter
    fun createItemFilter() = emptyFilterSupplier.get()

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking && hasCopyFlag(playerIn.getHeldItem(handIn))) {
                this.clearCopyFlag(playerIn.getHeldItem(handIn))
            } else {
                ItemGuiFactory.INSTANCE.open(playerIn as EntityPlayerMP, handIn)
            }
        }
        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        val tileEntity = world.getTileEntity(pos) ?: return EnumActionResult.PASS
        if (world.isRemote) return EnumActionResult.SUCCESS
        val filterApplicatable = tileEntity.getCapability(ClayiumTileCapabilities.ITEM_FILTER_APPLICATABLE, side)
            ?: return EnumActionResult.PASS

        val heldItem = player.getHeldItem(hand)
        if (hasCopyFlag(heldItem)) {
            val filterItemStack = filterApplicatable.createFilterStack(side)
                ?: return EnumActionResult.PASS
            setCopyFlag(filterItemStack)
            player.setHeldItem(hand, filterItemStack)
            return EnumActionResult.SUCCESS
        } else {
            val filterItem = heldItem.item as? ItemFilterBase ?: return EnumActionResult.PASS
            filterApplicatable.setFilter(side, this.createItemFilter(player.getHeldItem(hand)), filterItem, heldItem.tagCompound?.copy())
            return EnumActionResult.SUCCESS
        }
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return if (this.hasCopyFlag(stack)) {
            "${super.getTranslationKey(stack)}.copy"
        } else {
            super.getTranslationKey(stack)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        UtilLocale.formatTooltips(tooltip, "${this.translationKey}.tooltip")
    }

    protected open fun hasCopyFlag(stack: ItemStack): Boolean {
        return stack.tagCompound?.hasKey("copy") == true
    }

    private fun clearCopyFlag(stack: ItemStack) {
        stack.tagCompound?.removeTag("copy")
    }

    private fun setCopyFlag(stack: ItemStack) {
        val tag = stack.tagCompound ?: NBTTagCompound()
        tag.setBoolean("copy", true)
        stack.tagCompound = tag
    }

    /**
     * Creates an item filter instance from the given stack.
     * @param stack [ItemStack] of this [Item]
     */
    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object : ItemCapabilityProvider {
            override fun <T> getCapability(capability: Capability<T>): T? {
                return if (capability === ClayiumCapabilities.ITEM_FILTER) {
                    capability.cast(createItemFilter(stack))
                } else {
                    null
                }
            }
        }
    }
}