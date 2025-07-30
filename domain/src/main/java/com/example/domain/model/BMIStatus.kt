package com.example.domain.model

enum class BMIStatus(val maxValue: Float){
    UNDERWEIGHT(18.5f),
    NORMAL(25.0f),
    OVERWEIGHT(30.0f),
    OBESE(Float.MAX_VALUE)
}