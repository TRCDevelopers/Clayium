package com.github.trcdevelopers.clayium.common.recipe

import com.github.trcdevelopers.clayium.common.ClayEnergy

interface IMachineRecipe {
    val requiredTicks: Int
    val cePerTick: ClayEnergy
}