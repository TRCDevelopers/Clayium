package com.github.trcdevelopers.clayium.common.items.metaitem

import com.github.trcdevelopers.clayium.common.items.metaitem.component.TooltipBehavior
import com.github.trcdevelopers.clayium.common.util.UtilLocale

@Suppress("unused")
object MetaItemClayParts : MetaItemClayium("clay_parts") {

    //region Circuits
    val CLAY_CIRCUIT = addItem(0, "clay_circuit").tier(2)
    val SIMPLE_CIRCUIT = addItem(1, "simple_circuit").tier(3)
    val BASIC_CIRCUIT = addItem(2, "basic_circuit").tier(4)
    val ADVANCED_CIRCUIT = addItem(3, "advanced_circuit").tier(5).oreDict("circuitBasic")
    val PRECISION_CIRCUIT = addItem(4, "precision_circuit").tier(6).oreDict("circuitAdvanced")
    val INTEGRATED_CIRCUIT = addItem(5, "integrated_circuit").tier(7).oreDict("circuitElite")
    val CLAY_CORE = addItem(6, "clay_core").tier(8).oreDict("circuitUltimate")
        .addComponent(TooltipBehavior { it.addAll(UtilLocale.localizeTooltip("item.clayium.clay_core.tooltip")) })
    val CLAY_BRAIN = addItem(7, "clay_brain").tier(9)
    val CLAY_SPIRIT = addItem(8, "clay_spirit").tier(10)
    val CLAY_SOUL = addItem(9, "clay_soul").tier(11)
    val CLAY_ANIMA = addItem(10, "clay_anima").tier(12)
    val CLAY_PSYCHE = addItem(11, "clay_psyche").tier(13)

    val CLAY_CIRCUIT_BOARD = addItem(12, "clay_circuit_board").tier(2)
    val CEE_BOARD = addItem(13, "cee_board").tier(3)
    //endregion

    val CLAY_BEARING = addItem(14, "clay_bearing").tier(1)
    val CLAY_BLADE = addItem(15, "clay_blade").tier(1)
    val CLAY_CUTTING_HEAD = addItem(16, "clay_cutting_head").tier(1)
    val CLAY_CYLINDER = addItem(17, "clay_cylinder").tier(1)
    val CLAY_DISC = addItem(18, "clay_disc").tier(1)
    val CLAY_GEAR = addItem(19, "clay_gear").tier(1)
    val CLAY_GRINDING_HEAD = addItem(20, "clay_grinding_head").tier(1)
    val CLAY_NEEDLE = addItem(21, "clay_needle").tier(1)
    val CLAY_PIPE = addItem(22, "clay_pipe").tier(1)
    val CLAY_RING = addItem(24, "clay_ring").tier(1)
    val CLAY_SHORT_STICK = addItem(25, "clay_short_stick").tier(1)
    val CLAY_SMALL_DISC = addItem(26, "clay_small_disc").tier(1)
    val CLAY_SMALL_RING = addItem(27, "clay_small_ring").tier(1)
    val CLAY_SPINDLE = addItem(28, "clay_spindle").tier(1)
    val CLAY_STICK = addItem(29, "clay_stick").tier(1)
    val CLAY_WHEEL = addItem(30, "clay_wheel").tier(1)

    val DENSE_CLAY_BEARING = addItem(31, "dense_clay_bearing").tier(2)
    val DENSE_CLAY_BLADE = addItem(32, "dense_clay_blade").tier(2)
    val DENSE_CLAY_CUTTING_HEAD = addItem(33, "dense_clay_cutting_head").tier(2)
    val DENSE_CLAY_CYLINDER = addItem(34, "dense_clay_cylinder").tier(2)
    val DENSE_CLAY_DISC = addItem(35, "dense_clay_disc").tier(2)
    val DENSE_CLAY_GEAR = addItem(36, "dense_clay_gear").tier(2)
    val DENSE_CLAY_GRINDING_HEAD = addItem(37, "dense_clay_grinding_head").tier(2)
    val DENSE_CLAY_NEEDLE = addItem(38, "dense_clay_needle").tier(2)
    val DENSE_CLAY_PIPE = addItem(39, "dense_clay_pipe").tier(2)
    val DENSE_CLAY_RING = addItem(41, "dense_clay_ring").tier(2)
    val DENSE_CLAY_SHORT_STICK = addItem(42, "dense_clay_short_stick").tier(2)
    val DENSE_CLAY_SMALL_DISC = addItem(43, "dense_clay_small_disc").tier(2)
    val DENSE_CLAY_SMALL_RING = addItem(44, "dense_clay_small_ring").tier(2)
    val DENSE_CLAY_SPINDLE = addItem(45, "dense_clay_spindle").tier(2)
    val DENSE_CLAY_STICK = addItem(46, "dense_clay_stick").tier(2)
    val DENSE_CLAY_WHEEL = addItem(47, "dense_clay_wheel").tier(2)

    val LARGE_CLAY_BALL = addItem(48, "large_clay_ball").tier(2)

    val COMPRESSED_CLAY_SHARD = addItem(49, "compressed_clay_shard").tier(1)
    val INDUSTRIAL_CLAY_SHARD = addItem(50, "industrial_clay_shard").tier(2)
    val ADV_INDUSTRIAL_CLAY_SHARD = addItem(51, "adv_industrial_clay_shard").tier(3)

    val CLAY_GADGET_PARTS = addItem(52, "clay_gadget_parts").tier(6)

    val MANIPULATOR_MK1 = addItem(53, "manipulator_mk1").tier(6)
    val MANIPULATOR_MK2 = addItem(54, "manipulator_mk2").tier(8)
    val MANIPULATOR_MK3 = addItem(55, "manipulator_mk3").tier(12)

    val LASER_PARTS = addItem(56, "laser_parts").tier(7)
    val TELEPORTATION_PARTS = addItem(57, "teleportation_parts").tier(11)
}