package com.github.trc.clayium.api.capability

import com.github.trc.clayium.common.util.RayTraceMemory

interface IRayTraceMemoryApplicable {

    /**
     * @return success or failure.
     */
    fun acceptRayTraceMemory(rayTraceMemory: RayTraceMemory): Boolean
}