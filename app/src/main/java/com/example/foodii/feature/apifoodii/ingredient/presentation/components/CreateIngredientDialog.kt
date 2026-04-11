package com.example.foodii.feature.apifoodii.ingredient.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.compose.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private data class CreateIngredientFormState(
    val name: String = "",
    val calories: String = "",
    val error: String? = null,
)

@Composable
fun CreateIngredientDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    val formFlow = remember { MutableStateFlow(CreateIngredientFormState()) }
    val form by formFlow.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nuevo Ingrediente",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = primaryLight
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = form.name,
                    onValueChange = {
                        formFlow.update { s -> s.copy(name = it, error = null) }
                    },
                    label = { Text("Nombre del ingrediente") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryLight,
                        focusedLabelColor = primaryLight
                    )
                )

                OutlinedTextField(
                    value = form.calories,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            formFlow.update { s -> s.copy(calories = it, error = null) }
                        }
                    },
                    label = { Text("Calorías por 100g") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryLight,
                        focusedLabelColor = primaryLight
                    )
                )

                if (form.error != null) {
                    Text(
                        text = form.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val calValue = form.calories.toDoubleOrNull()
                    if (form.name.isBlank()) {
                        formFlow.update { it.copy(error = "El nombre no puede estar vacío") }
                    } else if (calValue == null) {
                        formFlow.update { it.copy(error = "Ingresa un número válido para las calorías") }
                    } else {
                        onConfirm(form.name, calValue)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryLight)
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = outlineLight)
            }
        },
        containerColor = backgroundLight
    )
}
