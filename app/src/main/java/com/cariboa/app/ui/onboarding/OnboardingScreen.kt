package com.cariboa.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.domain.model.TravelInterest
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState

private data class WelcomePage(
    val title: String,
    val subtitle: String,
    val animationState: BoaAnimationState,
)

private val welcomePages = listOf(
    WelcomePage(
        title = "Meet Boa, your travel companion",
        subtitle = "Your AI-powered guide to exploring the world with ease and delight.",
        animationState = BoaAnimationState.Idle,
    ),
    WelcomePage(
        title = "AI-Powered Itineraries",
        subtitle = "Tell us where and when — Boa crafts a personalized plan just for you.",
        animationState = BoaAnimationState.Thinking,
    ),
    WelcomePage(
        title = "Discover Hidden Gems",
        subtitle = "Boa finds what guidebooks miss — local secrets and off-the-beaten-path spots.",
        animationState = BoaAnimationState.Excited,
    ),
    WelcomePage(
        title = "Your Trips, Organized",
        subtitle = "Save, edit, and revisit your adventures whenever inspiration strikes.",
        animationState = BoaAnimationState.Celebrating,
    ),
)

private val totalPages = welcomePages.size + 1 // +1 for interests page
private val interestsPageIndex = welcomePages.size

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }

    val pagerState = rememberPagerState(pageCount = { totalPages })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            if (page < welcomePages.size) {
                val welcomePage = welcomePages[page]
                WelcomePageContent(
                    title = welcomePage.title,
                    subtitle = welcomePage.subtitle,
                    animationState = welcomePage.animationState,
                )
            } else {
                InterestsPageContent(
                    selectedInterests = uiState.selectedInterests,
                    onToggleInterest = viewModel::toggleInterest,
                )
            }
        }

        // Page indicator dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(totalPages) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                        ),
                )
            }
        }

        // "Let's Go!" button only on last page
        if (pagerState.currentPage == interestsPageIndex) {
            Button(
                onClick = viewModel::completeOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp),
            ) {
                Text("Let's Go!")
            }
        } else {
            Spacer(modifier = Modifier.height(56.dp + 32.dp)) // keep layout stable
        }
    }
}

@Composable
private fun WelcomePageContent(
    title: String,
    subtitle: String,
    animationState: BoaAnimationState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BoaAnimation(
            state = animationState,
            size = 220.dp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestsPageContent(
    selectedInterests: Set<TravelInterest>,
    onToggleInterest: (TravelInterest) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "What do you love to do?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pick your travel interests so Boa can personalize your experience.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            TravelInterest.entries.forEach { interest ->
                val selected = interest in selectedInterests
                FilterChip(
                    selected = selected,
                    onClick = { onToggleInterest(interest) },
                    label = {
                        Text(
                            text = interest.name.lowercase()
                                .replaceFirstChar { it.uppercaseChar() },
                        )
                    },
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }
        }
    }
}
