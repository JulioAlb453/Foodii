package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.*
import com.example.foodii.core.hardware.domain.ShakeDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScheduleComponent(
    shakeDetector: ShakeDetector,
    onDateSelected: (Long) -> Unit
) {
    val showDatePickerFlow = remember { MutableStateFlow(false) }
    val showDatePicker by showDatePickerFlow.collectAsStateWithLifecycle()
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DisposableEffect(Unit) {
            shakeDetector.startListening {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(it)
                    showDatePickerFlow.value = false
                }
            }
            onDispose {
                shakeDetector.stopListening()
            }
        }

        DatePickerDialog(
            onDismissRequest = { showDatePickerFlow.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onDateSelected(it)
                        }
                        showDatePickerFlow.value = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = primaryLight)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePickerFlow.value = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = outlineLight)
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = primaryLight,
                    headlineContentColor = primaryLight,
                    selectedDayContainerColor = primaryLight,
                    selectedDayContentColor = onPrimaryLight,
                    todayContentColor = primaryLight,
                    todayDateBorderColor = primaryLight
                )
            )
        }
    }

    ExtendedFloatingActionButton(
        onClick = { showDatePickerFlow.update { true } },
        containerColor = secondaryLight,
        contentColor = onSecondaryLight,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        text = { Text("Agendar Comida", fontWeight = FontWeight.Bold) }
    )
}
