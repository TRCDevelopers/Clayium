package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import com.github.trcdevelopers.clayium.api.capability.IConfigurationTool
import com.github.trcdevelopers.clayium.common.items.metaitem.component.IItemCapabilityProvider
import com.github.trcdevelopers.clayium.common.items.metaitem.component.TooltipBehavior
import com.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraftforge.common.capabilities.Capability

@Suppress("unused")
object MetaItemClayParts : MetaItemClayium("clay_parts") {

    //todo UpperCamel or lowerCamel
    //region Circuits
    val CLAY_CIRCUIT = addItem(0, "clay_circuit").tier(2)
    val SIMPLE_CIRCUIT = addItem(1, "simple_circuit").tier(3)
    val BASIC_CIRCUIT = addItem(2, "basic_circuit").tier(4)
    val ADVANCED_CIRCUIT = addItem(3, "advanced_circuit").tier(5).oreDict("circuitBasic")
    val PRECISION_CIRCUIT = addItem(4, "precision_circuit").tier(6).oreDict("circuitAdvanced")
    val INTEGRATED_CIRCUIT = addItem(5, "integrated_circuit").tier(7).oreDict("circuitElite")
    val CLAY_CORE = addItem(6, "clay_core").tier(8).oreDict("circuitUltimate")
        .tooltip("item.clayium.clay_core.tooltip")
    val CLAY_BRAIN = addItem(7, "clay_brain").tier(9)
    val CLAY_SPIRIT = addItem(8, "clay_spirit").tier(10)
    val CLAY_SOUL = addItem(9, "clay_soul").tier(11)
    val CLAY_ANIMA = addItem(10, "clay_anima").tier(12)
    val CLAY_PSYCHE = addItem(11, "clay_psyche").tier(13)

    val CLAY_CIRCUIT_BOARD = addItem(12, "clay_circuit_board").tier(2)
    val CEE_BOARD = addItem(13, "cee_board").tier(3)
    //endregion

    val LARGE_CLAY_BALL = addItem(14, "large_clay_ball").tier(2)

    val COMPRESSED_CLAY_SHARD = addItem(15, "compressed_clay_shard").tier(1)
    val INDUSTRIAL_CLAY_SHARD = addItem(16, "industrial_clay_shard").tier(2)
    val ADV_INDUSTRIAL_CLAY_SHARD = addItem(17, "adv_industrial_clay_shard").tier(3)

    val CLAY_GADGET_PARTS = addItem(18, "clay_gadget_parts").tier(6)

    val MANIPULATOR_MK1 = addItem(19, "manipulator_mk1").tier(6)
    val MANIPULATOR_MK2 = addItem(20, "manipulator_mk2").tier(8)
    val MANIPULATOR_MK3 = addItem(21, "manipulator_mk3").tier(12)

    val LaserParts = addItem(22, "laser_parts").tier(7)
    val SynchronousParts = addItem(23, "synchronous_parts").tier(9)
        .tooltip("item.clayium.synchronous_parts.tooltip")
    val TeleportationParts = addItem(24, "teleportation_parts").tier(11)

    val ANTIMATTER_SEED = addItem(25, "antimatter_seed").tier(9)

    val EnergizedClayDust = addItem(26, "energized_clay_dust").tier(3)
    val ExcitedClayDust = addItem(27, "excited_clay_dust").tier(7)
    val CEECircuit = addItem(28, "cee_circuit")
    val CEE = addItem(29, "cee")

    val RawClayRollingPin = createRawClayConfigTool(30, "raw_clay_rolling_pin").tier(1)
    val RawClaySlicer = createRawClayConfigTool(31, "raw_clay_slicer").tier(1)
    val RawClaySpatula = createRawClayConfigTool(32, "raw_clay_spatula").tier(1)

    private fun createRawClayConfigTool(meta: Short, name: String): MetaValueItem {
        return addItem(meta, name)
            .addComponent(TooltipBehavior { UtilLocale.formatTooltips(it, "item.clayium.filter_remover.tooltip") })
            .addComponent(object : IItemCapabilityProvider {
                override fun <T : Any> getCapability(capability: Capability<T>): T? {
                    return if (capability == ClayiumCapabilities.CONFIG_TOOL)
                        ClayiumCapabilities.CONFIG_TOOL.cast(IConfigurationTool { IConfigurationTool.ToolType.FILTER_REMOVER } )
                    else
                        null
                }
            })
    }
}