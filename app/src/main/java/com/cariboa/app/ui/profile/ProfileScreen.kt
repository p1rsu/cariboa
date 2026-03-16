package com.cariboa.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.domain.model.SubscriptionTier
import com.cariboa.app.domain.model.TravelInterest
import com.cariboa.app.domain.model.User
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState
import com.cariboa.app.ui.theme.MutedRed
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onUpgradeToPro: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteConfirmation) {
        DeleteAccountDialog(
            onConfirm = {
                viewModel.deleteAccount()
                onDeleteAccount()
            },
            onDismiss = { viewModel.dismissDeleteConfirmation() },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                uiState.user?.let { user ->
                    UserHeader(user = user)

                    Spacer(modifier = Modifier.height(24.dp))

                    SubscriptionCard(
                        user = user,
                        onUpgradeToPro = onUpgradeToPro,
                    )

                    if (user.interests.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        TravelPreferencesSection(interests = user.interests)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.signOut()
                        onSignOut()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Sign Out")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.showDeleteConfirmation() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MutedRed,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text("Delete Account")
                }
            }
        }
    }
}

@Composable
private fun UserHeader(user: User) {
    val initial = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = user.displayName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = user.email,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    )
}

@Composable
private fun SubscriptionCard(
    user: User,
    onUpgradeToPro: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (user.subscriptionTier == SubscriptionTier.PRO)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            else
                MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Subscription",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (user.subscriptionTier == SubscriptionTier.PRO)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                ) {
                    Text(
                        text = if (user.subscriptionTier == SubscriptionTier.PRO) "Pro" else "Trial",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (user.subscriptionTier == SubscriptionTier.PRO)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (user.subscriptionTier == SubscriptionTier.PRO && user.subscriptionExpiry != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Expires ${dateFormat.format(Date(user.subscriptionExpiry))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }

            if (user.subscriptionTier == SubscriptionTier.TRIAL) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onUpgradeToPro,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Upgrade to Pro")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TravelPreferencesSection(interests: List<TravelInterest>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Travel Interests",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            interests.forEach { interest ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = interest.name.lowercase()
                            .replaceFirstChar { it.uppercaseChar() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BoaAnimation(
                    state = BoaAnimationState.Confused,
                    size = 120.dp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Are you sure?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Deleting your account is permanent and cannot be undone. All your trips, itineraries, and data will be lost forever.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MutedRed,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text("Delete Account")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
