package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.creativetab.ClayiumCTabs
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple

class ItemFluidCapsule(
    private val capacity: Int,
    private val addSubItemsToCreativeTab: Boolean = false,
) : Item(), ICustomItemModel {

    init {
        setCreativeTab(ClayiumCTabs.fluidCapsules)
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack?>) {
        if (!this.isInCreativeTab(tab)) return

        items.add(ItemStack(this))
        if (this.addSubItemsToCreativeTab) {
            for (fluid in FluidRegistry.getRegisteredFluids().values) {
                val fluidStack = FluidStack(fluid, this.capacity)
                val itemStack = ItemStack(this)
                val fluidHandler = FluidHandlerItemStackSimple(itemStack, this.capacity)
                if (fluidHandler.fill(fluidStack, true) == fluidStack.amount) {
                    val filled = fluidHandler.container
                    items.add(filled)
                }
            }
        }
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider {
        return FluidHandlerItemStackSimple(stack, this.capacity)
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val fluidStack = getFluid(stack)
        if (fluidStack != null) {
            tooltip.add("${fluidStack.localizedName} ${this.capacity}mB")
        } else {
            tooltip.add("${this.capacity}mB")
        }
    }

    override fun registerModels() {
        val loc = ModelResourceLocation(clayiumId("fluid_capsule"), "capacity=${this.capacity}")
        ModelLoader.setCustomMeshDefinition(this) { loc }
        ModelBakery.registerItemVariants(this, loc)
    }

    fun getFluid(stack: ItemStack): FluidStack? {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
            ?.drain(Int.MAX_VALUE, false)
    }

    companion object {
        fun from(fluidStack: FluidStack): ItemStack {
            val itemStack = ItemStack(ClayiumItems.FLUID_CAPSULE_1000MB)
            val fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
                ?: return ItemStack.EMPTY
            fluidHandler.fill(fluidStack, true)
            return itemStack
        }

        fun water(): ItemStack {
            return from(FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME))
        }

        fun lava(): ItemStack {
            return from(FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME))
        }
    }
}