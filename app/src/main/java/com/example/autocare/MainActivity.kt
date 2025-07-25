package com.example.autocare

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.autocare.data.AuthRepository
import com.example.autocare.medicamento.MedicamentoViewModel
import com.example.autocare.telas.TelaDetalhesMedicamento
import com.example.autocare.ui.theme.AutoCareTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import com.example.autocare.medicamento.Medicamento
import com.example.autocare.receitas.telas.TelaDetalhesReceita
import com.example.autocare.telas.TelaAdicionarMedicamento
import com.example.autocare.receitas.telas.TelaAdicionarReceita
import com.example.autocare.telas.TelaAjudaSuporte
import com.example.autocare.telas.TelaCalendario
import com.example.autocare.telas.TelaConfiguracoes
import com.example.autocare.telas.TelaEditarMedicamento
import com.example.autocare.telas.TelaEditarReceita
import com.example.autocare.telas.TelaFavoritos
import com.example.autocare.receitas.telas.TelaReceitas
import com.example.autocare.ui.auth.ForgotPasswordScreen
import com.example.autocare.ui.auth.LoginScreen
import com.example.autocare.ui.auth.RegisterScreen
import com.example.autocare.viewmodel.AuthViewModel
import com.example.autocare.viewmodel.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authRepository = AuthRepository()
            val authViewModelFactory = AuthViewModelFactory(authRepository)
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            val medicamentoViewModel: MedicamentoViewModel = viewModel()

            val isDarkMode by medicamentoViewModel.darkModeEnabled.collectAsState()

            AutoCareTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppNavigation(
                        authViewModel = authViewModel,
                        medicamentoViewModel = medicamentoViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation(authViewModel: AuthViewModel, medicamentoViewModel: MedicamentoViewModel) {
    val navController = rememberNavController()
    val TAG = "AutoCareDebug"

    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        currentUser = firebaseAuth.currentUser
        Log.d(TAG, "AuthStateListener: Usuário mudou para -> ${currentUser?.email}")
    }

    DisposableEffect(FirebaseAuth.getInstance()) {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
        }
    }

    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentUser != null && (currentRoute == "login_route" || currentRoute == "register_route" || currentRoute == "forgot_password_route")) {
            Log.d(TAG, "Usuário detectado, navegando para home")
            navController.navigate("home") {
                popUpTo("login_route") { inclusive = true }
                launchSingleTop = true
            }
        } else if (currentUser == null && currentRoute != "login_route" && currentRoute != "register_route" && currentRoute != "forgot_password_route") {
            Log.d(TAG, "Usuário nulo, navegando para login")
            navController.navigate("login_route") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val startDestination = if (currentUser != null) "home" else "login_route"

    AppContent(
        navController = navController,
        startDestination = startDestination,
        authViewModel = authViewModel,
        medicamentoViewModel = medicamentoViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    medicamentoViewModel: MedicamentoViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authRoutes = setOf("login_route", "register_route", "forgot_password_route")
    val showAppBars = currentRoute !in authRoutes

    Scaffold(
        topBar = {
            if (showAppBars) {
                val title = when {
                    currentRoute == "home" -> "AutoCare"
                    currentRoute == "calendar_route" -> "Agenda de Medicamentos"
                    currentRoute?.startsWith("details") == true -> "Detalhes do Medicamento"
                    currentRoute?.startsWith("receita_details") == true -> "Detalhes da Receita"
                    currentRoute == "favorites_route" -> "Meus Favoritos"
                    currentRoute == "settings_route" -> "Configurações"
                    currentRoute == "help_route" -> "Ajuda e Suporte"
                    currentRoute == "add_medicamento_route" -> "Adicionar Medicamento"
                    currentRoute == "add_receita_route" -> "Adicionar Receita"
                    currentRoute?.startsWith("edit_medicamento_route") == true -> "Editar Medicamento"
                    currentRoute?.startsWith("edit_receita_route") == true -> "Editar Receita"
                    else -> ""
                }

                if (currentRoute == "home") {
                    MedicineTopAppBar(
                        appName = title,
                        onFavoriteClick = { navController.navigate("favorites_route") },
                        onSettingsClick = { navController.navigate("settings_route") },
                        onHelpClick = { navController.navigate("help_route") }
                    )
                } else {
                    DetailsTopAppBar(title = title, onBackClick = { navController.popBackStack() })
                }
            }
        },
        bottomBar = {
            val showBottomBar = currentRoute == "home" || currentRoute == "calendar_route" || currentRoute == "receitas_route" || currentRoute == "profile_route"
            if (showBottomBar) {
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
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("login_route") {
                LoginScreen(viewModel = authViewModel, navController = navController)
            }
            composable("register_route") {
                RegisterScreen(viewModel = authViewModel, navController = navController)
            }
            composable("forgot_password_route") {
                ForgotPasswordScreen(viewModel = authViewModel, navController = navController)
            }


            composable("home") {
                val medicamentos by medicamentoViewModel.medicamentos.collectAsState()
                TelaInicial(
                    medicamentos = medicamentos,
                    onMedicamentoClick = { medicamentoId -> navController.navigate("details/$medicamentoId") },
                    onRemoveMedicamentoClick = { medicamentoId -> medicamentoViewModel.removeMedicamento(medicamentoId) }
                )
            }
            composable(
                route = "details/{medicamentoId}",
                arguments = listOf(navArgument("medicamentoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val medicamentoId = backStackEntry.arguments?.getString("medicamentoId")
                val medicamentos by medicamentoViewModel.medicamentos.collectAsState()
                val medicamento = medicamentos.find { it.id == medicamentoId }
                if (medicamento != null) {
                    TelaDetalhesMedicamento(
                        medicamento = medicamento,
                        navController = navController,
                        medicamentoViewModel = medicamentoViewModel,
                        todosMedicamentos = medicamentos
                    )
                }
            }
            composable("favorites_route") {
                val medicamentosFavoritos by medicamentoViewModel.favoriteMedicamentos.collectAsState()
                TelaFavoritos(
                    medicamentosFavoritos = medicamentosFavoritos,
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
                }
            }

            composable("calendar_route") {
                val medicamentos by medicamentoViewModel.medicamentos.collectAsState()
                TelaCalendario(
                    medicamentos = medicamentos,
                    getAgendamentosParaDia = { data, meds ->
                        medicamentoViewModel.getAgendamentosParaDia(data, meds)
                    }
                )
            }
            composable("receitas_route") {
                val receitas by medicamentoViewModel.receitas.collectAsState()
                TelaReceitas(
                    receitas = receitas,
                    onReceitaClick = { receitaId -> navController.navigate("receita_details/$receitaId") },
                    onAddReceitaClick = { navController.navigate("add_receita_route") }
                )
            }
            composable(
                route = "receita_details/{receitaId}",
                arguments = listOf(navArgument("receitaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val receitaId = backStackEntry.arguments?.getString("receitaId")
                val receita = receitaId?.let { medicamentoViewModel.getReceitaById(it) }
                if (receita != null) {
                    TelaDetalhesReceita(
                        receita = receita,
                        onEditClick = { navController.navigate("edit_receita_route/${receita.id}") },
                        onDeleteClick = {
                            medicamentoViewModel.removeReceita(receita.id)
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable("add_receita_route") {
                TelaAdicionarReceita(
                    medicamentoViewModel = medicamentoViewModel,
                    onReceitaAdded = { navController.popBackStack() }
                )
            }
            composable(
                route = "edit_receita_route/{receitaId}",
                arguments = listOf(navArgument("receitaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val receitaId = backStackEntry.arguments?.getString("receitaId")
                if (receitaId != null) {
                    TelaEditarReceita(
                        receitaId = receitaId,
                        medicamentoViewModel = medicamentoViewModel,
                        onReceitaUpdated = { navController.popBackStack() }
                    )
                }
            }
            composable("profile_route") {
                ProfileScreen(onLogoutClick = { authViewModel.logout() })
            }
        }
    }
}


@Composable
fun ProfileScreen(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Perfil do Usuário", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Sair (Logout)")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineTopAppBar(appName: String, onFavoriteClick: () -> Unit, onSettingsClick: () -> Unit, onHelpClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(appName, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, "Mais opções") }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Favoritos") }, onClick = { onFavoriteClick(); expanded = false }, leadingIcon = { Icon(Icons.Default.Favorite, null) })
                DropdownMenuItem(text = { Text("Configurações") }, onClick = { onSettingsClick(); expanded = false }, leadingIcon = { Icon(Icons.Default.Settings, null) })
                DropdownMenuItem(text = { Text("Ajuda") }, onClick = { onHelpClick(); expanded = false }, leadingIcon = { Icon(Icons.Default.Help, null) })
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
            IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Voltar") }
        }
    )
}

@Composable
fun MedicineBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        val items = listOf("Início", "Calendário", "Receitas", "Perfil")
        val icons = listOf(Icons.Default.Home, Icons.Default.CalendarToday, Icons.Default.AssignmentTurnedIn, Icons.Default.Help)
        val routes = listOf("home", "calendar_route", "receitas_route", "profile_route")
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], item) },
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
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaInicial(modifier: Modifier = Modifier, medicamentos: List<Medicamento>, onMedicamentoClick: (String) -> Unit, onRemoveMedicamentoClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredMedicamentos = remember(medicamentos, searchQuery) {
        if (searchQuery.isBlank()) medicamentos else medicamentos.filter {
            it.nome.contains(searchQuery, ignoreCase = true) || it.descricaoCurta.contains(searchQuery, ignoreCase = true)
        }
    }
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, label = { Text("Buscar medicamentos...") }, leadingIcon = { Icon(Icons.Default.Search, "Ícone de Busca") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (filteredMedicamentos.isEmpty()) {
                item {
                    Text(text = if (searchQuery.isBlank()) "Nenhum medicamento adicionado." else "Nenhum resultado para \"$searchQuery\".", modifier = Modifier.fillMaxWidth().padding(top = 32.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(filteredMedicamentos) { medicamento ->
                    MedicamentoCard(medicamento = medicamento, onClick = { onMedicamentoClick(medicamento.id) }, onDeleteClick = { onRemoveMedicamentoClick(medicamento.id) })
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
    Card(modifier = Modifier.fillMaxWidth().wrapContentHeight(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (!medicamento.imageUrl.isNullOrBlank()) {
                AsyncImage(model = Uri.parse(medicamento.imageUrl), contentDescription = "Imagem de ${medicamento.nome}", modifier = Modifier.size(80.dp).padding(end = 16.dp), contentScale = ContentScale.Crop, error = painterResource(id = R.drawable.ic_launcher_foreground), placeholder = painterResource(id = R.drawable.ic_launcher_foreground))
            } else {
                val localImageResId = medicamento.imageResId
                if (localImageResId != null && localImageResId != 0) {
                    Image(painter = painterResource(id = localImageResId), contentDescription = "Imagem de ${medicamento.nome}", modifier = Modifier.size(80.dp).padding(end = 16.dp), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.size(80.dp).padding(end = 16.dp).background(Color.LightGray, shape = MaterialTheme.shapes.small), contentAlignment = Alignment.Center) {
                        Text("Sem Imagem", fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = medicamento.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = medicamento.descricaoCurta, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Frequência: ${medicamento.frequencia.intervaloHoras}h, Início: ${medicamento.frequencia.primeiraHora}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Excluir medicamento", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir '${medicamento.nome}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick(medicamento.id)
                        showDeleteDialog = false
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
