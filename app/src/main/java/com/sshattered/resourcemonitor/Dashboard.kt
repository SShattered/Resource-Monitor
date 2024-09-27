package com.sshattered.resourcemonitor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Dashboard (
    @Json(name = "cpuusage")
    val cpuUsage: Double = 0.0,
    @Json(name = "frequency")
    val cpuFrequency: Double = 0.0,
    @Json(name = "cputemperature")
    val cpuTemp: Double = 0.0,
    @Json(name = "ramusage")
    val ramUsage: Double = 0.0,
    @Json(name = "gpuusage")
    val gpuUsage: Double = 0.0,
    @Json(name = "vramusage")
    val vRamUsage: Double = 0.0,
    @Json(name = "gputemperature")
    val gpuTemp: Double = 0.0,
    @Json(name = "gpupower")
    val gpuPower: Double = 0.0)