package com.github.trc.clayium.common.items.metaitem

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IItemGadget
import com.github.trc.clayium.common.capability.impl.ClayGadgetAutoEat
import com.github.trc.clayium.common.capability.impl.ClayGadgetFlight
import com.github.trc.clayium.common.capability.impl.ClayGadgetHealth
import com.github.trc.clayium.common.capability.impl.ClayGadgetLongArm
import com.github.trc.clayium.common.capability.impl.ClayGadgetOverclock
import com.github.trc.clayium.common.capability.impl.GadgetRepeatedlyAttack
import com.github.trc.clayium.common.items.metaitem.component.IItemCapabilityProvider
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

object MetaItemClayGadget : MetaItemClayium("clay_gadget") {

    init {
        maxStackSize = 1
    }

    val OverclockMk1 = addItem(0, "gadget_overclocker_mk1").tier(10)
        .gadget(ClayGadgetOverclock(3))
    val OverclockMk2 = addItem(1, "gadget_overclocker_mk2").tier(11)
        .gadget(ClayGadgetOverclock(2))
    val OverclockMk3 = addItem(2, "gadget_overclocker_mk3").tier(12)
        .gadget(ClayGadgetOverclock(1))
    val OverclockMk4 = addItem(3, "gadget_overclocker_mk4").tier(13)
        .gadget(ClayGadgetOverclock(0))

    val FlightMk1 = addItem(100, "gadget_flight_mk1").tier(12)
        .gadget(ClayGadgetFlight(0))
    val FlightMk2 = addItem(101, "gadget_flight_mk2").tier(13)
        .gadget(ClayGadgetFlight(1))
    val FlightMk3 = addItem(102, "gadget_flight_mk3").tier(13)
        .gadget(ClayGadgetFlight(2))

    val HealthMk1 = addItem(200, "gadget_health_mk1").tier(6)
        .gadget(ClayGadgetHealth(20.0))
    val HealthMk2 = addItem(201, "gadget_health_mk2").tier(10)
        .gadget(ClayGadgetHealth(80.0))
    val HealthMk3 = addItem(202, "gadget_health_mk3").tier(12)
        .gadget(ClayGadgetHealth(180.0))

    val AutoEatEconomical = addItem(300, "gadget_auto_eat_economical").tier(7)
        .gadget(ClayGadgetAutoEat(economicalMode = true))
    val AutoEat = addItem(301, "gadget_auto_eat").tier(7)
        .gadget(ClayGadgetAutoEat(economicalMode = false))

    val RepeatedlyAttack = addItem(400, "gadget_repeatedly_attack").tier(10)
        .gadget(GadgetRepeatedlyAttack)

    val LongArmMk1 = addItem(500, "gadget_long_arm_mk1").tier(6)
        .gadget(ClayGadgetLongArm(3.0))
    val LongArmMk2 = addItem(501, "gadget_long_arm_mk2").tier(8)
        .gadget(ClayGadgetLongArm(7.0))
    val LongArmMk3 = addItem(502, "gadget_long_arm_mk3").tier(12)
        .gadget(ClayGadgetLongArm(20.0))

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        UtilLocale.formatTooltips(tooltip, "${this.getTranslationKey(stack)}.tooltip")
    }

    private fun MetaValueItem.gadget(clayGadget: IItemGadget): MetaValueItem {
        return this.addComponent(object: IItemCapabilityProvider {
            override fun <T : Any> getCapability(capability: Capability<T>): T? {
                if (capability === ClayiumCapabilities.CLAY_GADGET) {
                    return capability.cast(clayGadget)
                }
                return null
            }
        })
    }
}