package com.example.presentation.features.auth.onboarding.models

sealed class OnboardingStep(val stepNumber: Int) {
    data object Welcome : OnboardingStep(0)
    data object Name : OnboardingStep(1)
    data object GoalSelection : OnboardingStep(2)
    data object WeightChange : OnboardingStep(3)
    data object CurrentWeight : OnboardingStep(4)
    data object Height : OnboardingStep(5)
    data object Gender : OnboardingStep(6)
    data object BirthDate : OnboardingStep(7)
    data object ActivityLevel : OnboardingStep(8)
    data object Result : OnboardingStep(9)
}