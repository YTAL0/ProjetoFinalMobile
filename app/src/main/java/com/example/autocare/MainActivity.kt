package com.example.autocare


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autocare.ui.theme.AutoCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val medicamentoViewModel: MedicamentoViewModel = viewModel()
            AutoCareTheme(darkTheme = medicamentoViewModel.darkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(medicamentoViewModel = medicamentoViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(medicamentoViewModel: MedicamentoViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) },
        topBar = {
            when (currentRoute) {
                "home" -> MedicineTopAppBar(
                    appName = "AutoCare",
                    onFavoriteClick = { navController.navigate("favorites_route") },
                    onSettingsClick = { navController.navigate("settings_route") },
                    onHelpClick = { navController.navigate("help_route") }
                )
                currentRoute -> if (currentRoute?.startsWith("details") == true) {
                    DetailsTopAppBar(
                        title = "Detalhes do Medicamento",
                        onBackClick = { navController.popBackStack() }
                    )
                } else if (currentRoute == "favorites_route") {
                    DetailsTopAppBar(
                        title = "Meus Favoritos",
                        onBackClick = { navController.popBackStack() }
                    )
                } else if (currentRoute == "settings_route") {
                    DetailsTopAppBar(
                        title = "Configurações",
                        onBackClick = { navController.popBackStack() }
                    )
                } else if (currentRoute == "help_route") {
                    DetailsTopAppBar(
                        title = "Ajuda e Suporte",
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        },
        bottomBar = {
            if (currentRoute == "home") {
                MedicineBottomNavigation(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                TelaInicial(
                    medicamentos = getSampleMedicamentos(),
                    onMedicamentoClick = { medicamentoId ->
                        navController.navigate("details/$medicamentoId")
                    }
                )
            }
            composable(
                route = "details/{medicamentoId}",
                arguments = listOf(navArgument("medicamentoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val medicamentoId = backStackEntry.arguments?.getString("medicamentoId")
                val medicamento = getSampleMedicamentos().find { it.id == medicamentoId }
                if (medicamento != null) {
                    TelaDetalhesMedicamento(
                        medicamento = medicamento,
                        navController = navController,
                        medicamentoViewModel = medicamentoViewModel
                    )
                } else {
                    Text(
                        "Medicamento não encontrado!",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
            composable("favorites_route") {
                TelaFavoritos(
                    medicamentosFavoritos = medicamentoViewModel.favoriteMedicamentos,
                    onRemoveFavoriteClick = { medicamento -> medicamentoViewModel.removeFavorite(medicamento) },
                    onMedicamentoClick = { medicamentoId -> navController.navigate("details/$medicamentoId") }
                )
            }
            composable("settings_route") {
                TelaConfiguracoes(medicamentoViewModel = medicamentoViewModel)
            }
            composable("help_route") {
                TelaAjudaSuporte()
            }

            composable("calendar_route") { Text("Tela de Calendário", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) }
            composable("reminders_route") { Text("Tela de Lembretes", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) }
            composable("profile_route") { Text("Tela de Perfil", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineTopAppBar(
    appName: String,
    onFavoriteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(appName, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Mais opções")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Favoritos") },
                    onClick = { onFavoriteClick(); expanded = false },
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Configurações") },
                    onClick = { onSettingsClick(); expanded = false },
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Ajuda") },
                    onClick = { onHelpClick(); expanded = false },
                    leadingIcon = { Icon(Icons.Default.Help, contentDescription = null) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopAppBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
            }
        }
    )
}

@Composable
fun MedicineBottomNavigation(navController: NavController) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        val items = listOf("Início", "Calendário", "Lembretes", "Perfil")
        val icons = listOf(Icons.Default.Home, Icons.Default.CalendarToday, Icons.Default.AddAlert, Icons.Default.Help)
        val routes = listOf("home", "calendar_route", "reminders_route", "profile_route")

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = currentRoute == routes[index],
                onClick = {
                    if (currentRoute != routes[index]) {
                        navController.navigate(routes[index]) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    Toast.makeText(context, "Navegou para: $item", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaInicial(
    modifier: Modifier = Modifier,
    medicamentos: List<Medicamento>,
    onMedicamentoClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredMedicamentos = remember(medicamentos, searchQuery) {
        if (searchQuery.isBlank()) {
            medicamentos
        } else {
            medicamentos.filter {
                it.nome.contains(searchQuery, ignoreCase = true) ||
                        it.descricaoCurta.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar medicamentos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ícone de Busca") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredMedicamentos.isEmpty() && searchQuery.isNotBlank()) {
                item {
                    Text(
                        text = "Nenhum medicamento encontrado para \"$searchQuery\".",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(filteredMedicamentos) { medicamento ->
                    MedicamentoCard(
                        medicamento = medicamento,
                        onClick = { onMedicamentoClick(medicamento.id) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoCard(medicamento: Medicamento, onClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = medicamento.imageResId),
                contentDescription = "Imagem de ${medicamento.nome}",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = medicamento.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = medicamento.descricaoCurta, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Dosagem: ${medicamento.dosagem}", fontSize = 12.sp, color = Color.DarkGray)
                Text(text = "Frequência: ${medicamento.frequencia}", fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AutoCareTheme {
        AppNavigation(medicamentoViewModel = MedicamentoViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun MedicamentoCardPreview() {
    AutoCareTheme {
        MedicamentoCard(
            medicamento = Medicamento(
                id = "prev_001",
                nome = "Vitamina C",
                descricaoCurta = "Suplemento para imunidade.",
                imageResId = R.drawable.sivastatina,
                dosagem = "1000mg",
                frequencia = "1 vez ao dia"
            ),
            onClick = {}
        )
    }
}
