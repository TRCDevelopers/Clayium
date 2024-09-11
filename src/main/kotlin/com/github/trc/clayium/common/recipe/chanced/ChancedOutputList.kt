package com.github.trc.clayium.common.recipe.chanced

class ChancedOutputList<RESULT>(
    val chancedOutputs: List<IChancedOutput<RESULT>>,
    val chancedLogic: IChancedOutputLogic,
) : Iterable<IChancedOutput<RESULT>> {
    fun roll(): List<RESULT> {
        return chancedLogic.roll(chancedOutputs)
    }

    override fun iterator(): Iterator<IChancedOutput<RESULT>> {
        return chancedOutputs.iterator()
    }

    companion object {
        val WEIGHTED = object : IChancedOutputLogic {
            override fun <R, T : IChancedOutput<R>> roll(chancedOutputs: List<T>): List<R> {
                val totalWeight = chancedOutputs.sumOf { it.chance }
                val pos = (1..totalWeight).random()
                var currentPos = 0
                for (chancedOutput in chancedOutputs) {
                    currentPos += chancedOutput.chance
                    if (pos < currentPos) {
                        return listOf(chancedOutput.result)
                    }
                }
                return emptyList()
            }
        }

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