package com.example.foodii.feature.apifoodii.meal.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private data class IngredientLine(
    val ingredientId: String,
    val name: String,
    val grams: Int,
)

private data class CreateMealFormState(
    val name: String = "",
    val dateStr: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val mealTime: FoodiiMealTime = FoodiiMealTime.LUNCH,
    val stepsText: String = "",
    val ingredientMenuExpanded: Boolean = false,
    val selectedIngredientId: String? = null,
    val gramsInput: String = "",
    val lines: List<IngredientLine> = emptyList(),
)

@Composable
fun CreateMealDialog(
    show: Boolean,
    ingredientsCatalog: List<Ingredient>,
    isLoading: Boolean,
    errorMessage: String?,
    successData: FoodiiMeal?,
    onDismiss: () -> Unit,
    onClearError: () -> Unit,
    onConsumedSuccess: () -> Unit,
    onSave: (
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredients: List<Pair<String, Int>>,
        steps: List<String>,
    ) -> Unit,
) {
    if (!show) return

    LaunchedEffect(successData) {
        if (successData != null) {
            onConsumedSuccess()
            onDismiss()
        }
    }

    val formFlow = remember { MutableStateFlow(CreateMealFormState()) }
    val form by formFlow.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        },
        title = { Text("Nuevo platillo") },
        text = {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    TextButton(onClick = onClearError) { Text("Cerrar aviso") }
                }

                OutlinedTextField(
                    value = form.name,
                    onValueChange = { formFlow.update { s -> s.copy(name = it) } },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = form.dateStr,
                    onValueChange = { formFlow.update { s -> s.copy(dateStr = it) } },
                    label = { Text("Fecha (AAAA-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Momento del día", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    FoodiiMealTime.entries.forEach { t ->
                        FilterChip(
                            selected = form.mealTime == t,
                            onClick = { formFlow.update { s -> s.copy(mealTime = t) } },
                            label = {
                                Text(
                                    t.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                        )
                    }
                }

                Text("Ingredientes", style = MaterialTheme.typography.titleSmall)
                form.lines.forEach { line ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("${line.name} — ${line.grams} g", modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            formFlow.update { s ->
                                s.copy(lines = s.lines.filter { it != line })
                            }
                        }) { Text("Quitar") }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { formFlow.update { it.copy(ingredientMenuExpanded = true) } },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            ingredientsCatalog.find { it.id == form.selectedIngredientId }?.name
                                ?: "Elegir ingrediente…",
                        )
                    }
                    DropdownMenu(
                        expanded = form.ingredientMenuExpanded,
                        onDismissRequest = {
                            formFlow.update { it.copy(ingredientMenuExpanded = false) }
                        },
                    ) {
                        ingredientsCatalog.forEach { ing ->
                            DropdownMenuItem(
                                text = { Text(ing.name) },
                                onClick = {
                                    formFlow.update {
                                        it.copy(
                                            selectedIngredientId = ing.id,
                                            ingredientMenuExpanded = false,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = form.gramsInput,
                    onValueChange = {
                        formFlow.update { s ->
                            s.copy(gramsInput = it.filter { ch -> ch.isDigit() })
                        }
                    },
                    label = { Text("Cantidad (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedButton(
                    onClick = {
                        val id = form.selectedIngredientId ?: return@OutlinedButton
                        val g = form.gramsInput.toIntOrNull() ?: return@OutlinedButton
                        if (g <= 0) return@OutlinedButton
                        val ing = ingredientsCatalog.find { it.id == id } ?: return@OutlinedButton
                        formFlow.update { s ->
                            s.copy(
                                lines = s.lines + IngredientLine(ingredientId = id, name = ing.name, grams = g),
                                gramsInput = "",
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Añadir ingrediente") }

                OutlinedTextField(
                    value = form.stepsText,
                    onValueChange = { formFlow.update { s -> s.copy(stepsText = it) } },
                    label = { Text("Instrucciones (una por línea)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isLoading,
                onClick = {
                    val date = runCatching {
                        LocalDate.parse(form.dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                    }.getOrNull()
                    if (date == null) return@TextButton
                    val steps = form.stepsText.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    onSave(
                        form.name,
                        date,
                        form.mealTime,
                        form.lines.map { it.ingredientId to it.grams },
                        steps,
                    )
                },
            ) { Text(if (isLoading) "Guardando…" else "Crear") }
        },
        dismissButton = {
            TextButton(enabled = !isLoading, onClick = onDismiss) { Text("Cancelar") }
        },
    )
}
