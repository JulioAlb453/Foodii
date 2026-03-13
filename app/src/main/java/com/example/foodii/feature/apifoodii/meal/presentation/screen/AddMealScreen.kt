package com.example.foodii.feature.apifoodii.meal.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.compose.*
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModel
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    viewModel: MealFoodiiViewModel,
    ingredientViewModel: IngredientViewModel,
    userId: String,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var mealName by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf(FoodiiMealTime.LUNCH) }
    var showIngredientDialog by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    val selectedIngredients by viewModel.selectedIngredients.collectAsState()
    val capturedImageUri by viewModel.capturedImageUri.collectAsState()
    val ingredientUiState by ingredientViewModel.uiState.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            Toast.makeText(context, "Captura cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onTakePhoto { uri ->
                uri?.let { cameraLauncher.launch(it) }
            }
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(uiState.successData) {
        if (uiState.successData != null) {
            Toast.makeText(context, "¡Platillo creado con éxito!", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight,
                    titleContentColor = onPrimaryLight,
                    navigationIconContentColor = onPrimaryLight
                ),
                title = { Text("Nueva Receta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading) {
                ExtendedFloatingActionButton(
                    onClick = { 
                        val ingredientsList = selectedIngredients.map { it.first.id to it.second }
                        viewModel.saveMeal(mealName, LocalDate.now(), selectedTime, ingredientsList, userId, capturedImageUri)
                    },
                    containerColor = primaryLight,
                    contentColor = onPrimaryLight,
                    icon = { Icon(Icons.Default.Save, contentDescription = null) },
                    text = { Text("Guardar Platillo") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = mealName,
                    onValueChange = { mealName = it },
                    label = { Text("Nombre del platillo") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Momento del día:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FoodiiMealTime.entries.forEach { time ->
                        FilterChip(
                            selected = selectedTime == time,
                            onClick = { if (!uiState.isLoading) selectedTime = time },
                            label = { Text(time.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { 
                        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            viewModel.onTakePhoto { uri -> uri?.let { cameraLauncher.launch(it) } }
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (capturedImageUri == null) secondaryLight else primaryLight
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (capturedImageUri == null) "Añadir foto del platillo" else "¡Foto lista! ✅")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Ingredientes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { if (!uiState.isLoading) showIngredientDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Añadir")
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(selectedIngredients) { (ingredient, amount) ->
                        ListItem(
                            headlineContent = { Text(ingredient.name) },
                            supportingContent = { Text("${amount}g") },
                            trailingContent = {
                                IconButton(
                                    enabled = !uiState.isLoading,
                                    onClick = { viewModel.removeIngredientFromDraft(ingredient.id) }
                                ) {
                                    Icon(Icons.Default.Delete, tint = errorLight, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = primaryLight)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Subiendo receta...", fontWeight = FontWeight.Bold)
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Cerrar", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                ) { Text(text = error) }
            }
        }
    }

    if (showIngredientDialog) {
        AlertDialog(
            onDismissRequest = { showIngredientDialog = false },
            title = { Text("Elegir del Catálogo") },
            text = {
                Box(modifier = Modifier.height(300.dp)) {
                    LazyColumn {
                        items(ingredientUiState.ingredients) { ingredient ->
                            ListItem(
                                modifier = Modifier.clickable { 
                                    viewModel.addIngredientToDraft(ingredient, 100)
                                    showIngredientDialog = false
                                },
                                headlineContent = { Text(ingredient.name) },
                                supportingContent = { Text("${ingredient.caloriesPer100g} kcal") }
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showIngredientDialog = false }) { Text("Cerrar") } }
        )
    }
}
