package com.example.foodii.feature.apifoodii.ingredient.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.*
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.presentation.components.CreateIngredientDialog
import com.example.foodii.feature.apifoodii.ingredient.presentation.components.EditIngredientDialog
import com.example.foodii.feature.apifoodii.ingredient.presentation.components.IngredientItemCard
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    viewModel: IngredientViewModel,
    onBackPressed: () -> Unit
) {
    FoodiiTheme(darkTheme = true, dynamicColor = false) {
        val uiState by viewModel.uiState.collectAsState()
        var searchQuery by remember { mutableStateOf("") }
        var showCreateDialog by remember { mutableStateOf(false) }
        var editingIngredient by remember { mutableStateOf<Ingredient?>(null) }

        val filteredIngredients = remember(searchQuery, uiState.ingredients) {
            if (searchQuery.isBlank()) {
                uiState.ingredients
            } else {
                uiState.ingredients.filter { 
                    it.name.contains(searchQuery, ignoreCase = true) 
                }
            }
        }

        if (showCreateDialog) {
            CreateIngredientDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, calories ->
                    viewModel.createIngredient(name, calories)
                    showCreateDialog = false
                }
            )
        }

        editingIngredient?.let { ingredient ->
            EditIngredientDialog(
                ingredient = ingredient,
                onDismiss = { editingIngredient = null },
                onConfirm = { updatedIngredient ->
                    viewModel.updateIngredient(updatedIngredient)
                    editingIngredient = null
                }
            )
        }

        Scaffold(
            containerColor = backgroundLight,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = primaryLight,
                    contentColor = onPrimaryLight
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Ingrediente")
                }
            },
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = primaryDark,
                            titleContentColor = onPrimaryDark,
                            navigationIconContentColor = onPrimaryDark
                        ),
                        title = { Text("Catálogo de Ingredientes", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    )
                    
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            placeholder = {
                                Text("Buscar ingrediente...", color = primaryLight)
                            },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = onPrimaryDark,
                                unfocusedBorderColor =onPrimaryDark,
                                focusedTextColor = primaryLight,
                                unfocusedTextColor = primaryDark,
                                cursorColor = onPrimaryLight
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = primaryDark
                        )
                    }
                    uiState.error != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "¡Ups! Algo salió mal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.error ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Button(onClick = { viewModel.loadIngredients() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                    filteredIngredients.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) "No hay ingredientes" else "No se encontraron resultados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "Resultados (${filteredIngredients.size})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(filteredIngredients) { ingredient ->
                                IngredientItemCard(
                                    ingredient = ingredient,
                                    onClick = { editingIngredient = ingredient }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
