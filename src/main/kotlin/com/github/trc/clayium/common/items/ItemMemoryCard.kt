package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.blocks.machine.MachineIoMode
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemMemoryCard : Item() {
    init {
        maxStackSize = 1
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        val metaTileEntity = world.getMetaTileEntity(pos)
        if (metaTileEntity == null) return EnumActionResult.PASS
        if (world.isRemote) return EnumActionResult.SUCCESS

        val itemStack = player.getHeldItem(hand)
        val stackTag = itemStack.tagCompound

        if (player.isSneaking) {
            itemStack.tagCompound = createMachineIoNbt(metaTileEntity)
            player.sendMessage(TextComponentTranslation("item.clayium.memory_card.copied"))
        } else if (stackTag != null && isTagValid(stackTag)) {
            if (applyToMetaTileEntity(stackTag, metaTileEntity)) {
                player.sendMessage(TextComponentTranslation("item.clayium.memory_card.pasted"))
            } else {
                player.sendMessage(TextComponentTranslation("item.clayium.memory_card.failed"))
            }
        }
        return EnumActionResult.SUCCESS
    }

    private fun isTagValid(tag: NBTTagCompound): Boolean {
        for (side in EnumFacing.entries) {
            val i = side.index
            if (!(tag.hasKey("input$i", Constants.NBT.TAG_INT) || tag.hasKey("output$i", Constants.NBT.TAG_INT))) {
                return false
            }
        }
        return true
    }

    private fun createMachineIoNbt(metaTileEntity: MetaTileEntity): NBTTagCompound {
        val tag = NBTTagCompound()
        for (side in EnumFacing.entries) {
            val i = side.index
            val input = metaTileEntity.getInput(side)
            val output = metaTileEntity.getOutput(side)
            tag.setInteger("input$i", input.id)
            tag.setInteger("output$i", output.id)
        }
        return tag
    }

    private fun applyToMetaTileEntity(tag: NBTTagCompound, metaTileEntity: MetaTileEntity): Boolean {
        val inputs = EnumFacing.entries.map { MachineIoMode.byId(tag.getInteger("input${it.index}")) }
        val outputs = EnumFacing.entries.map { MachineIoMode.byId(tag.getInteger("output${it.index}")) }

        val io = inputs.zip(outputs)

        if (io.any { (i, o) -> !(metaTileEntity.isInputModeValid(i) && metaTileEntity.isOutputModeValid(o)) }) {
            return false
        }

        io.forEachIndexed { index, (input, output) ->
            val side = EnumFacing.byIndex(index)
            metaTileEntity.setInput(side, input)
            metaTileEntity.setOutput(side, output)
        }
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        UtilLocale.formatTooltips(tooltip, "item.clayium.memory_card.tooltip")
    }
}