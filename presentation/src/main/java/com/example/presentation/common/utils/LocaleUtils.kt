package com.example.presentation.common.utils

import java.util.Locale

fun getAppLocale(): Locale {
    return if (Locale.getDefault().language == "uk") {
        Locale.forLanguageTag("uk")
    } else {
        Locale.ENGLISH
    }
}