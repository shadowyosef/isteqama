package com.example.util

import kotlin.math.*

object QiblaCalculator {
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206

    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val phiK = Math.toRadians(KAABA_LATITUDE)
        val lambdaK = Math.toRadians(KAABA_LONGITUDE)
        val phi = Math.toRadians(userLat)
        val lambda = Math.toRadians(userLng)

        val deltaLambda = lambdaK - lambda
        val y = sin(deltaLambda)
        val x = cos(phi) * tan(phiK) - sin(phi) * cos(deltaLambda)

        var qibla = Math.toDegrees(atan2(y, x)).toFloat()
        if (qibla < 0) qibla += 360f
        return qibla
    }

    fun calculateDistanceToKaabaKm(userLat: Double, userLng: Double): Int {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(KAABA_LATITUDE - userLat)
        val dLng = Math.toRadians(KAABA_LONGITUDE - userLng)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(userLat)) * cos(Math.toRadians(KAABA_LATITUDE)) * sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).roundToInt()
    }
}
