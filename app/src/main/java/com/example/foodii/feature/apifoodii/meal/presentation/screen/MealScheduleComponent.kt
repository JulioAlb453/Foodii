package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.*
import com.example.foodii.core.hardware.domain.ShakeDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScheduleComponent(
    shakeDetector: ShakeDetector,
    onDateSelected: (Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DisposableEffect(Unit) {
            shakeDetector.startListening {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(it)
                    showDatePicker = false
                }
            }
            onDispose {
                shakeDetector.stopListening()
            }
        }

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onDateSelected(it)
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = primaryLight)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
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
        onClick = { showDatePicker = true },
        containerColor = secondaryLight,
        contentColor = onSecondaryLight,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        text = { Text("Agendar Comida", fontWeight = FontWeight.Bold) }
    )
}
