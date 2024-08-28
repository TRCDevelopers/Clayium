package com.github.trc.clayium.api.laser

interface IClayLaser {
    val red: Int
    val green: Int
    val blue: Int

    val energy: Double
    val age: Int
}