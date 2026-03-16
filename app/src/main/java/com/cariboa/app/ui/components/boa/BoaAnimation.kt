package com.cariboa.app.ui.components.boa

import android.provider.Settings
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun BoaAnimation(
    state: BoaAnimationState,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
) {
    val context = LocalContext.current
    val animationsEnabled = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) > 0f
    }

    Crossfade(
        targetState = state,
        animationSpec = tween(300),
        label = "boa_transition",
    ) { currentState ->
        if (animationsEnabled) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.Asset(currentState.lottieAsset)
            )
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = modifier
                    .size(size)
                    .semantics { contentDescription = currentState.contentDescription },
            )
        } else {
            Text(
                text = currentState.contentDescription,
                modifier = modifier.semantics {
                    contentDescription = currentState.contentDescription
                },
            )
        }
    }
}
