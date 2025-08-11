package com.example.presentation.features.auth.onboarding.models

sealed class OnboardingStep(val stepNumber: Int) {
    data object Welcome : OnboardingStep(0)
    data object GoalSelection : OnboardingStep(1)
    data object WeightChange : OnboardingStep(2)
    data object CurrentWeight : OnboardingStep(3)
    data object Height : OnboardingStep(4)
    data object Gender : OnboardingStep(5)
    data object BirthDate : OnboardingStep(6)
    data object ActivityLevel : OnboardingStep(7)
    data object Result : OnboardingStep(8)
}