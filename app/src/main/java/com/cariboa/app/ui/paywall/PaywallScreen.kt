package com.cariboa.app.ui.paywall

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState
import com.cariboa.app.ui.theme.ForestGreen
import com.cariboa.app.ui.theme.WarmBrown

private val PRODUCT_MONTHLY = "cariboa_pro_monthly"
private val PRODUCT_YEARLY = "cariboa_pro_yearly"

private data class FeatureRow(
    val label: String,
    val trial: String,
    val pro: String,
)

private val featureRows = listOf(
    FeatureRow("Trip Plans", "3 total", "Unlimited"),
    FeatureRow("AI Itineraries", "3 total", "Unlimited"),
    FeatureRow("Hidden Gems", "Locked", "Full access"),
    FeatureRow("Hotel Search", "Locked", "Full access"),
    FeatureRow("Offline Access", "None", "Full offline"),
    FeatureRow("Priority Support", "—", "Included"),
)

@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel(),
) {
    // Hard paywall — intercept the back button and do nothing
    BackHandler(enabled = true) { }

    val uiState by viewModel.uiState.collectAsState()
    var isYearly by remember { mutableStateOf(true) }

    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Something went wrong") },
            text = { Text(uiState.error ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Boa celebrating with PRO badge overlay
            Box(contentAlignment = Alignment.BottomEnd) {
                BoaAnimation(
                    state = BoaAnimationState.Celebrating,
                    size = 160.dp,
                )
                ProBadge()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = if (uiState.isWelcomeBack) "Welcome Back!" else "Unlock Cariboa Pro",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState.isWelcomeBack)
                    "Pick up where you left off with full Pro access."
                else
                    "You've reached your trial limit. Upgrade to keep exploring.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Feature comparison table
            FeatureComparisonTable()

            Spacer(modifier = Modifier.height(28.dp))

            // Plan toggle
            PlanToggle(isYearly = isYearly, onToggle = { isYearly = it })

            Spacer(modifier = Modifier.height(24.dp))

            // Subscribe button
            Button(
                onClick = {
                    viewModel.onSubscribeClicked(
                        if (isYearly) PRODUCT_YEARLY else PRODUCT_MONTHLY,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isPurchasing,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                if (uiState.isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = if (isYearly) "Subscribe — $39.99 / year" else "Subscribe — $4.99 / month",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Restore purchases
            TextButton(
                onClick = { viewModel.onRestorePurchases() },
                enabled = !uiState.isPurchasing,
            ) {
                Text(
                    text = "Restore Purchases",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recurring billing. Cancel anytime in the Play Store.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ProBadge() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(WarmBrown),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "PRO",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun FeatureComparisonTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Feature",
                    modifier = Modifier.weight(1.5f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
                Text(
                    text = "Trial",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
                Text(
                    text = "Pro",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            featureRows.forEachIndexed { index, row ->
                FeatureTableRow(row = row)
                if (index < featureRows.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureTableRow(row: FeatureRow) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = row.label,
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = row.trial,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
        if (row.pro == "Unlimited" || row.pro == "Full access" || row.pro == "Full offline" || row.pro == "Included") {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = row.pro,
                    tint = ForestGreen,
                    modifier = Modifier.size(18.dp),
                )
            }
        } else {
            Text(
                text = row.pro,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun PlanToggle(
    isYearly: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PlanCard(
            modifier = Modifier.weight(1f),
            title = "Monthly",
            price = "$4.99",
            subtitle = "per month",
            isSelected = !isYearly,
            badge = null,
            onClick = { onToggle(false) },
        )
        PlanCard(
            modifier = Modifier.weight(1f),
            title = "Yearly",
            price = "$39.99",
            subtitle = "per year",
            isSelected = isYearly,
            badge = "Best Value",
            onClick = { onToggle(true) },
        )
    }
}

@Composable
private fun PlanCard(
    modifier: Modifier,
    title: String,
    price: String,
    subtitle: String,
    isSelected: Boolean,
    badge: String?,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "planBorder",
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "planBg",
    )

    Card(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp),
            ),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = price,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }

            if (badge != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = WarmBrown,
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
