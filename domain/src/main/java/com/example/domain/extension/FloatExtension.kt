package com.example.domain.extension

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float?.roundTo1Decimal(decimalPlaces: Int = 1): Float? {
    if (this == null) return null
    val factor = 10.0f.pow(decimalPlaces)
    return (this * factor).roundToInt() / factor
}