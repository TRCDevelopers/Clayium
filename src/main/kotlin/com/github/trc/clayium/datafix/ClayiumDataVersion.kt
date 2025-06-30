package com.github.trc.clayium.datafix

enum class ClayiumDataVersion {
    V0,
    V1_MORE_FILTERS,
    ;

    companion object {
        val currentVersion = entries[entries.size - 1]
    }
}