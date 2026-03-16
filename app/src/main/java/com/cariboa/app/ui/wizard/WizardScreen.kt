package com.cariboa.app.ui.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.domain.model.BudgetLevel
import com.cariboa.app.domain.model.TravelInterest
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WizardScreen(
    onNavigateToItinerary: (String) -> Unit,
    onNavigateToPaywall: () -> Unit,
    viewModel: WizardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // React to successful generation
    LaunchedEffect(uiState.generatedTripId) {
        uiState.generatedTripId?.let { tripId ->
            onNavigateToItinerary(tripId)
        }
    }

    // React to paywall trigger
    LaunchedEffect(uiState.showPaywall) {
        if (uiState.showPaywall) {
            onNavigateToPaywall()
            viewModel.dismissPaywall()
        }
    }

    // Error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isGenerating) {
            GeneratingContent(modifier = Modifier.padding(paddingValues))
        } else {
            WizardContent(
                uiState = uiState,
                onSetDestination = viewModel::setDestination,
                onSetDates = viewModel::setDates,
                onSetTravelers = viewModel::setTravelers,
                onToggleInterest = viewModel::toggleInterest,
                onSetBudget = viewModel::setBudget,
                onNext = viewModel::nextStep,
                onBack = viewModel::previousStep,
                onGenerate = viewModel::generate,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun WizardContent(
    uiState: WizardUiState,
    onSetDestination: (String) -> Unit,
    onSetDates: (Long?, Long?) -> Unit,
    onSetTravelers: (Int) -> Unit,
    onToggleInterest: (TravelInterest) -> Unit,
    onSetBudget: (BudgetLevel) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        // Stepper indicator
        StepperIndicator(
            currentStep = uiState.currentStep,
            totalSteps = 4,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        )

        // Step content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            when (uiState.currentStep) {
                0 -> Step0Destination(
                    destination = uiState.destination,
                    onDestinationChange = onSetDestination,
                )
                1 -> Step1DatesTravelers(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    travelers = uiState.travelers,
                    onDatesSelected = onSetDates,
                    onTravelersChange = onSetTravelers,
                )
                2 -> Step2Interests(
                    selectedInterests = uiState.interests,
                    onToggleInterest = onToggleInterest,
                )
                3 -> Step3Budget(
                    selectedBudget = uiState.budgetLevel,
                    onSelectBudget = onSetBudget,
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.currentStep > 0) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (uiState.currentStep < 3) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    enabled = uiState.destination.isNotBlank() || uiState.currentStep > 0,
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = onGenerate,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Generate")
                }
            }
        }
    }
}

@Composable
private fun StepperIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val stepLabels = listOf("Destination", "Dates", "Interests", "Budget")
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(totalSteps) { index ->
            val isCompleted = index < currentStep
            val isCurrent = index == currentStep
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = when {
                            isCompleted || isCurrent -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isCompleted) "✓" else "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCompleted || isCurrent)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stepLabels.getOrElse(index) { "" },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (index < totalSteps - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(bottom = 20.dp),
                    color = if (index < currentStep)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

@Composable
private fun Step0Destination(
    destination: String,
    onDestinationChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoaAnimation(
            state = BoaAnimationState.Idle,
            size = 160.dp,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Where are you going?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tell Boa your dream destination.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = destination,
            onValueChange = onDestinationChange,
            label = { Text("Destination") },
            placeholder = { Text("e.g. Tokyo, Japan") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1DatesTravelers(
    startDate: Long?,
    endDate: Long?,
    travelers: Int,
    onDatesSelected: (Long?, Long?) -> Unit,
    onTravelersChange: (Int) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "When are you traveling?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick your travel dates and group size.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Date range display / trigger
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
            val dateText = when {
                startDate != null && endDate != null ->
                    "${dateFormatter.format(Date(startDate))} → ${dateFormatter.format(Date(endDate))}"
                startDate != null ->
                    "${dateFormatter.format(Date(startDate))} → Select end date"
                else -> "Select travel dates"
            }
            Text(dateText)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Travelers counter
        Text(
            text = "Travelers",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilledIconButton(
                onClick = { onTravelersChange(travelers - 1) },
                enabled = travelers > 1,
            ) {
                Text("−", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "$travelers",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(24.dp))
            FilledIconButton(
                onClick = { onTravelersChange(travelers + 1) },
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (travelers == 1) "1 traveler" else "$travelers travelers",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate,
            initialSelectedEndDateMillis = endDate,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDatesSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis,
                    )
                    showDatePicker = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step2Interests(
    selectedInterests: Set<TravelInterest>,
    onToggleInterest: (TravelInterest) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "What do you love to do?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick your interests so Boa can tailor the perfect itinerary.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
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

@Composable
private fun Step3Budget(
    selectedBudget: BudgetLevel,
    onSelectBudget: (BudgetLevel) -> Unit,
) {
    data class BudgetOption(
        val level: BudgetLevel,
        val label: String,
        val description: String,
        val emoji: String,
    )

    val options = listOf(
        BudgetOption(BudgetLevel.BUDGET, "Budget", "Hostels, street food, free attractions", "💰"),
        BudgetOption(BudgetLevel.MODERATE, "Moderate", "Mid-range hotels, local restaurants", "💳"),
        BudgetOption(BudgetLevel.PREMIUM, "Premium", "Luxury hotels, fine dining, VIP experiences", "✨"),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "What's your budget style?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Boa will tailor recommendations to match your spending style.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        options.forEach { option ->
            val isSelected = selectedBudget == option.level
            Card(
                onClick = { onSelectBudget(option.level) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = option.emoji,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = option.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneratingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BoaAnimation(
            state = BoaAnimationState.Thinking,
            size = 220.dp,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Boa is planning your trip...",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This may take a moment. Great adventures are worth the wait!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        CircularProgressIndicator()
    }
}
