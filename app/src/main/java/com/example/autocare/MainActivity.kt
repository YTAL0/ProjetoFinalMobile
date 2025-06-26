package com.example.autocare

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.AsyncImage
import com.example.autocare.com.example.autocare.medicamento.MedicamentoViewModel
import com.example.autocare.com.example.autocare.telas.TelaDetalhesMedicamento
import com.example.autocare.ui.theme.AutoCareTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AssignmentTurnedIn

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
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permissão de notificações concedida!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permissão de notificações negada.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
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
                } else if (currentRoute == "add_medicamento_route") {
                    DetailsTopAppBar(
                        title = "Adicionar Medicamento",
                        onBackClick = { navController.popBackStack() }
                    )
                } else if (currentRoute?.startsWith("edit_medicamento_route") == true) {
                    DetailsTopAppBar(
                        title = "Editar Medicamento",
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        },
        bottomBar = {
            if (currentRoute == "home") {
                MedicineBottomNavigation(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == "home") {
                FloatingActionButton(
                    onClick = { navController.navigate("add_medicamento_route") },
                    shape = CircleShape,
                    containerColor = Color(0xFFFF5555)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Medicamento")
                }
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
                    medicamentos = medicamentoViewModel.medicamentos,
                    onMedicamentoClick = { medicamentoId ->
                        navController.navigate("details/$medicamentoId")
                    },
                    // A chamada aqui já está correta!
                    onRemoveMedicamentoClick = { medicamentoId ->
                        medicamentoViewModel.removeMedicamento(medicamentoId)
                    }
                )
            }
            composable(
                route = "details/{medicamentoId}",
                arguments = listOf(navArgument("medicamentoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val medicamentoId = backStackEntry.arguments?.getString("medicamentoId")
                val medicamento = medicamentoViewModel.medicamentos.find { it.id == medicamentoId }
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
            composable("add_medicamento_route") {
                TelaAdicionarMedicamento(
                    medicamentoViewModel = medicamentoViewModel,
                    onMedicamentoAdded = { navController.popBackStack() }
                )
            }
            composable(
                route = "edit_medicamento_route/{medicamentoId}",
                arguments = listOf(navArgument("medicamentoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val medicamentoId = backStackEntry.arguments?.getString("medicamentoId")
                if (medicamentoId != null) {
                    TelaEditarMedicamento(
                        medicamentoId = medicamentoId,
                        medicamentoViewModel = medicamentoViewModel,
                        onMedicamentoUpdated = { navController.popBackStack() }
                    )
                } else {
                    Text(
                        "ID do medicamento para edição não encontrado!",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }

            composable("calendar_route") { Text("Tela de Calendário", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) }
            composable("reminders_route") { Text("Tela de Receitas", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) }
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
        val items = listOf("Início", "Calendário", "Receitas", "Perfil")
        val icons = listOf(Icons.Default.Home, Icons.Default.CalendarToday, Icons.Default.AssignmentTurnedIn, Icons.Default.Help)
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
    onMedicamentoClick: (String) -> Unit,
    onRemoveMedicamentoClick: (String) -> Unit
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
            } else if (filteredMedicamentos.isEmpty() && searchQuery.isBlank()) {
                item {
                    Text(
                        text = "Nenhum medicamento adicionado ainda. Clique no '+' para adicionar um!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else {
                items(filteredMedicamentos) { medicamento ->
                    MedicamentoCard(
                        medicamento = medicamento,
                        onClick = { onMedicamentoClick(medicamento.id) },
                        onDeleteClick = { onRemoveMedicamentoClick(medicamento.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoCard(medicamento: Medicamento, onClick: () -> Unit, onDeleteClick: (String) -> Unit) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

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
            if (!medicamento.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(medicamento.imageUrl),
                    contentDescription = "Imagem de ${medicamento.nome}",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            } else if (medicamento.imageResId != 0) {
                Image(
                    painter = painterResource(id = medicamento.imageResId),
                    contentDescription = "Imagem de ${medicamento.nome}",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sem Imagem", fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = medicamento.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = medicamento.descricaoCurta, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Frequência: ${medicamento.frequencia.intervaloHoras}h, Início: ${medicamento.frequencia.primeiraHora}", fontSize = 12.sp, color = Color.DarkGray)
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir medicamento",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir '${medicamento.nome}' da sua lista de medicamentos?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick(medicamento.id)
                        showDeleteDialog = false
                        Toast.makeText(context, "'${medicamento.nome}' excluído.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AutoCareTheme {
        AppNavigation(medicamentoViewModel = MedicamentoViewModel(LocalContext.current.applicationContext as Application))
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
                imageResId = R.drawable.paracetamol,
                dosagem = "1000mg",
                frequencia = Frequencia(24, "09:00")
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}