package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.item.IClayFluidCapsule
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import io.github.trcdevelopers.clayium.common.config.FluidCapsuleCreativeTabMode
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
    override val capacity: Int,
    private val addSubItemsToCreativeTab: Boolean = false,
) : Item(), ICustomItemModel, IClayFluidCapsule {

    init {
        setCreativeTab(ClayiumCTabs.fluidCapsules)
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack?>) {
        if (!this.isInCreativeTab(tab)) return

        items.add(ItemStack(this))
        if (ConfigCore.misc.fluidCapsuleCreativeTabMode == FluidCapsuleCreativeTabMode.ONLY_EMPTY) {
            return
        }
        if (ConfigCore.misc.fluidCapsuleCreativeTabMode == FluidCapsuleCreativeTabMode.ONLY_1000MB && !this.addSubItemsToCreativeTab) {
            return
        }
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

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider {
        return FluidHandlerItemStackSimple(stack, this.capacity)
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val fluidStack = getFluid(stack)
        if (fluidStack != null) {
            tooltip.add("${this.capacity}mB ${fluidStack.localizedName}")
        } else {
            tooltip.add("${this.capacity}mB")
        }
    }

    override fun registerModels() {
        val loc = ModelResourceLocation(clayiumId("fluid_capsule"), "capacity=${this.capacity}")
        ModelLoader.setCustomMeshDefinition(this) { loc }
        ModelBakery.registerItemVariants(this, loc)
    }

    override fun getFluid(stack: ItemStack): FluidStack? {
        return (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) as? FluidHandlerItemStackSimple)?.fluid
    }

    /**
     * returns a new ItemStack with count 1 and the specified fluid filled in it.
     */
    fun setFluid(fluid: Fluid): ItemStack {
        val itemStack = ItemStack(this)
        val fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
            ?: return ItemStack.EMPTY
        fluidHandler.fill(FluidStack(fluid, this.capacity), true)
        return itemStack
    }

    companion object {

        const val MAX_CAPACITY = Fluid.BUCKET_VOLUME

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