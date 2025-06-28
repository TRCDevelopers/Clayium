package com.github.trc.clayium.datafix

enum class ClayiumDataVersion {
    V0,
    V1_FILTER_REGISTRY,
    ;

    companion object {
        val currentVersion = entries[entries.size - 1]
    }
}