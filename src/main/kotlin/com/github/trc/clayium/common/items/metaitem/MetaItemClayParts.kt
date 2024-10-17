package com.github.trc.clayium.common.items.metaitem

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.IConfigurationTool
import com.github.trc.clayium.common.items.metaitem.component.IItemCapabilityProvider
import com.github.trc.clayium.common.items.metaitem.component.TooltipBehavior
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraftforge.common.capabilities.Capability

@Suppress("unused")
object MetaItemClayParts : MetaItemClayium("clay_parts") {

    // region Circuits
    val ClayCircuit = addItem(0, "clay_circuit").tier(2)
    val SimpleCircuit = addItem(1, "simple_circuit").tier(3)
    val BasicCircuit = addItem(2, "basic_circuit").tier(4)
    val AdvancedCircuit = addItem(3, "advanced_circuit").tier(5).oreDict("circuitBasic")
    val PrecisionCircuit = addItem(4, "precision_circuit").tier(6).oreDict("circuitAdvanced")
    val IntegratedCircuit = addItem(5, "integrated_circuit").tier(7).oreDict("circuitElite")
    val ClayCore =
        addItem(6, "clay_core")
            .tier(8)
            .oreDict("circuitUltimate")
            .tooltip("item.clayium.clay_core.tooltip")
    val ClayBrain = addItem(7, "clay_brain").tier(9)
    val ClaySpirit = addItem(8, "clay_spirit").tier(10)
    val ClaySoul = addItem(9, "clay_soul").tier(11)
    val ClayAnima = addItem(10, "clay_anima").tier(12)
    val ClayPsyche = addItem(11, "clay_psyche").tier(13)

    val ClayCircuitBoard = addItem(12, "clay_circuit_board").tier(2)
    val CeeBoard = addItem(13, "cee_board").tier(3)
    // endregion

    val LargeClayBall = addItem(14, "large_clay_ball").tier(2)

    val CompressedClayShard = addItem(15, "compressed_clay_shard").tier(1)
    val IndustrialClayShard = addItem(16, "industrial_clay_shard").tier(2)
    val AdvancedIndustrialClayShard = addItem(17, "adv_industrial_clay_shard").tier(3)

    val ClayGadgetParts = addItem(18, "clay_gadget_parts").tier(6)

    val ManipulatorMk1 = addItem(19, "manipulator_mk1").tier(6)
    val ManipulatorMk2 = addItem(20, "manipulator_mk2").tier(8)
    val ManipulatorMk3 = addItem(21, "manipulator_mk3").tier(12)

    val LaserParts = addItem(22, "laser_parts").tier(7)
    val SynchronousParts =
        addItem(23, "synchronous_parts").tier(9).tooltip("item.clayium.synchronous_parts.tooltip")
    val TeleportationParts = addItem(24, "teleportation_parts").tier(11)

    val AntimatterSeed = addItem(25, "antimatter_seed").tier(9)

    val EnergizedClayDust = addItem(26, "energized_clay_dust").tier(3)
    val ExcitedClayDust = addItem(27, "excited_clay_dust").tier(7)
    val CeeCircuit = addItem(28, "cee_circuit")
    val CEE = addItem(29, "cee")

    val RawClayRollingPin = createRawClayConfigTool(30, "raw_clay_rolling_pin").tier(1)
    val RawClaySlicer = createRawClayConfigTool(31, "raw_clay_slicer").tier(1)
    val RawClaySpatula = createRawClayConfigTool(32, "raw_clay_spatula").tier(1)

    private fun createRawClayConfigTool(meta: Short, name: String): MetaValueItem {
        return addItem(meta, name)
            .addComponent(
                TooltipBehavior {
                    UtilLocale.formatTooltips(it, "item.clayium.filter_remover.tooltip")
                }
            )
            .addComponent(
                object : IItemCapabilityProvider {
                    override fun <T : Any> getCapability(capability: Capability<T>): T? {
                        return if (capability == ClayiumCapabilities.CONFIG_TOOL)
                            ClayiumCapabilities.CONFIG_TOOL.cast(
                                IConfigurationTool { IConfigurationTool.ToolType.FILTER_REMOVER }
                            )
                        else null
                    }
                }
            )
    }
}
