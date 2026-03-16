package com.cariboa.app.ui.splash

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState
import com.cariboa.app.ui.theme.ForestGreen
import com.cariboa.app.ui.theme.ForestGreenDark
import com.cariboa.app.ui.theme.LightTan
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val authReady by viewModel.authReady.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        delay(2000L)
        viewModel.markTimerComplete()
    }

    LaunchedEffect(authReady) {
        if (authReady) {
            if (isLoggedIn) {
                onNavigateToHome()
            } else {
                onNavigateToOnboarding()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ForestGreenDark, ForestGreen, LightTan),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BoaAnimation(
                state = BoaAnimationState.Idle,
                size = 220.dp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cariboa",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LightTan,
                    fontSize = 48.sp,
                ),
            )
        }
    }
}
