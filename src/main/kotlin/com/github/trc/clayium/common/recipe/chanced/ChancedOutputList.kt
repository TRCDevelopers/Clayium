package com.github.trc.clayium.common.recipe.chanced

class ChancedOutputList<RESULT>(
    val chancedOutputs: List<IChancedOutput<RESULT>>,
    val chancedLogic: IChancedOutputLogic,
) {
    fun roll(): List<RESULT> {
        return chancedLogic.roll(chancedOutputs)
    }

    companion object {
        val XOR = object : IChancedOutputLogic {
            override fun <R, T : IChancedOutput<R>> roll(chancedOutputs: List<T>): List<R> {
                for (chancedOutput in chancedOutputs) {
                    if (passesChance(chancedOutput.chance)) {
                        return listOf(chancedOutput.result)
                    }
                }
                return emptyList()
            }
        }
    }
}