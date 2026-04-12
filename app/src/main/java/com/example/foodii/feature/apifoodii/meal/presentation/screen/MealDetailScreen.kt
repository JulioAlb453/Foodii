package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.compose.*
import com.example.foodii.core.utils.toFullImageUrl
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel
import com.example.ui.theme.TypographyFoodii
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    viewModel: MealFoodiiViewModel,
    mealId: String,
    userId: String,
    onBackPressed: () -> Unit
) {
    val meal by viewModel.selectedMeal.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(mealId) {
        viewModel.loadMealDetail(mealId, userId)
    }

    Scaffold(
        containerColor = backgroundLight,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight,
                    titleContentColor = onPrimaryLight,
                    navigationIconContentColor = onPrimaryLight
                ),
                title = { Text("Detalle de Receta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (meal != null) {
                MealScheduleComponent(
                    shakeDetector = viewModel.shakeDetector,
                    onDateSelected = { dateMillis ->
                        viewModel.scheduleMealReminder(meal!!, dateMillis)
                        scope.launch {
                            snackbarHostState.showSnackbar("Comida agendada correctamente")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryLight
                )
            } else if (meal == null) {
                Text(
                    text = uiState.error ?: "No se pudo cargar la información",
                    modifier = Modifier.align(Alignment.Center),
                    color = errorLight
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // SECCIÓN DE IMAGEN
                    val imageUrl = meal!!.image.toFullImageUrl()
                    if (imageUrl.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                            ) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = meal!!.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = primaryLight),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = meal!!.name,
                                    style = TypographyFoodii.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = onPrimaryLight
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = onPrimaryLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = meal!!.mealTime.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = onPrimaryLight
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceVariantLight),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = primaryLight,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Restaurant,
                                        contentDescription = null,
                                        modifier = Modifier.padding(12.dp),
                                        tint = onPrimaryLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Aporte Energético Total",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = onSurfaceVariantLight
                                    )
                                    Text(
                                        "${meal!!.totalCalories.toInt()} kcal",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = primaryLight
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Ingredientes usados",
                            style = TypographyFoodii.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp),
                            color = onBackgroundLight
                        )
                    }

                    items(meal!!.ingredients) { ingredient ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceContainerLight),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ingredient.name,
                                        style = TypographyFoodii.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = onSurfaceLight
                                    )
                                    Text(
                                        text = "Cantidad: ${ingredient.amount}g",
                                        style = TypographyFoodii.bodySmall,
                                        color = outlineLight
                                    )
                                }
                                Text(
                                    text = "${ingredient.calories.toInt()} kcal",
                                    style = TypographyFoodii.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryLight
                                )
                            }
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                        Text(
                            text = "Instrucciones de Preparación",
                            style = TypographyFoodii.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = primaryLight
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    val sortedSteps = meal!!.steps.sortedBy { it.stepOrder }
                    val stepsPerPage = 3
                    if (sortedSteps.isEmpty()) {
                        item {
                            Text(
                                "No hay pasos registrados para esta receta.",
                                style = TypographyFoodii.bodyMedium,
                                color = outlineLight
                            )
                        }
                    } else if (sortedSteps.size > 4) {
                        // VISTA DE LIBRO (PAGINADA) - 3 pasos por página
                        item {
                            val pageCount = (sortedSteps.size + stepsPerPage - 1) / stepsPerPage
                            val pagerState = rememberPagerState(pageCount = { pageCount })
                            
                            DisposableEffect(Unit) {
                                viewModel.shakeDetector.startListening {
                                    scope.launch {
                                        val nextPage = (pagerState.currentPage + 1) % pageCount
                                        pagerState.animateScrollToPage(nextPage)
                                    }
                                }
                                onDispose {
                                    viewModel.shakeDetector.stopListening()
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = surfaceContainerLight),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                        verticalAlignment = Alignment.Top
                                    ) { pageIndex ->
                                        val startIndex = pageIndex * stepsPerPage
                                        val pageSteps = sortedSteps.subList(startIndex, minOf(startIndex + stepsPerPage, sortedSteps.size))
                                        
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                        ) {
                                            pageSteps.forEachIndexed { idx, step ->
                                                if (idx > 0) Spacer(Modifier.height(16.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        color = primaryLight,
                                                        shape = RoundedCornerShape(4.dp),
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "${step.stepOrder}",
                                                                color = onPrimaryLight,
                                                                style = TypographyFoodii.labelSmall,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                    Spacer(Modifier.width(12.dp))
                                                    Text(
                                                        text = "PASO ${step.stepOrder}",
                                                        style = TypographyFoodii.labelLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryLight
                                                    )
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                Text(
                                                    text = step.description,
                                                    style = TypographyFoodii.bodyLarge.copy(lineHeight = 22.sp),
                                                    textAlign = TextAlign.Justify
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "Página ${pagerState.currentPage + 1} de $pageCount (Sacude para cambiar)",
                                        style = TypographyFoodii.labelSmall,
                                        color = outlineLight
                                    )
                                }
                            }
                        }
                    } else {
                        // VISTA DE UNA SOLA TARJETA (Hasta 4 pasos)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = surfaceContainerLight),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    sortedSteps.forEachIndexed { index, step ->
                                        if (index > 0) Spacer(Modifier.height(16.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = primaryLight,
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${step.stepOrder}",
                                                        color = onPrimaryLight,
                                                        style = TypographyFoodii.labelSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                text = "PASO ${step.stepOrder}",
                                                style = TypographyFoodii.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryLight
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = step.description,
                                            style = TypographyFoodii.bodyLarge.copy(lineHeight = 22.sp),
                                            textAlign = TextAlign.Justify
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("FIN DE LA RECETA", style = TypographyFoodii.labelSmall, color = outlineLight)
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
