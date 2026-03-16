package com.cariboa.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Google Fonts (androidx.compose.ui:ui-text-google-fonts) is not in the build dependencies.
// Using SansSerif as a fallback that closely resembles Nunito's rounded style.
val NunitoFamily: FontFamily = FontFamily.SansSerif

val CaribouTypography = Typography(
    headlineLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = NunitoFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = NunitoFamily, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
)
