package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.common.util.RayTraceMemory

interface IRayTraceMemoryApplicable {

    /**
     * @return success or failure.
     */
    fun acceptRayTraceMemory(rayTraceMemory: RayTraceMemory): Boolean
}