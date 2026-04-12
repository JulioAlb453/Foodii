package com.example.foodii.feature.apifoodii.meal.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    var stepInput by remember { mutableStateOf("") }
    val stepsList = remember { mutableStateListOf<String>() }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedIngredients by viewModel.selectedIngredients.collectAsStateWithLifecycle()
    val capturedImageUri by viewModel.capturedImageUri.collectAsStateWithLifecycle()
    val ingredientUiState by ingredientViewModel.uiState.collectAsStateWithLifecycle()

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

    LaunchedEffect(Unit) {
        ingredientViewModel.loadIngredients()
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
                        if (mealName.isBlank()) {
                            Toast.makeText(context, "Ingresa un nombre", Toast.LENGTH_SHORT).show()
                            return@ExtendedFloatingActionButton
                        }
                        val ingredientsList = selectedIngredients.map { it.first.id to it.second }
                        viewModel.saveMeal(
                            name = mealName,
                            date = LocalDate.now(),
                            mealTime = selectedTime,
                            ingredients = ingredientsList,
                            steps = stepsList.toList(),
                            userId = userId,
                            imageUri = capturedImageUri
                        )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("Nombre del platillo") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    )
                }

                item {
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
                }

                item {
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
                        )
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (capturedImageUri == null) "Añadir foto" else "¡Foto lista! ✅")
                    }
                }

                item {
                    HorizontalDivider()
                    Text("Pasos de la receta", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = stepInput,
                            onValueChange = { stepInput = it },
                            label = { Text("Nuevo paso...") },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isLoading
                        )
                        IconButton(onClick = {
                            if (stepInput.isNotBlank()) {
                                stepsList.add(stepInput)
                                stepInput = ""
                            }
                        }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Añadir", tint = primaryLight)
                        }
                    }
                }

                itemsIndexed(stepsList) { index, step ->
                    ListItem(
                        headlineContent = { Text("${index + 1}. $step") },
                        trailingContent = {
                            IconButton(onClick = { stepsList.removeAt(index) }) {
                                Icon(Icons.Default.Delete, tint = errorLight, contentDescription = null)
                            }
                        }
                    )
                }

                item {
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Ingredientes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { if (!uiState.isLoading) showIngredientDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Añadir")
                        }
                    }
                }

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
                        Text("Procesando...", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showIngredientDialog) {
        AlertDialog(
            onDismissRequest = { showIngredientDialog = false },
            title = { Text("Catálogo de Ingredientes") },
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
