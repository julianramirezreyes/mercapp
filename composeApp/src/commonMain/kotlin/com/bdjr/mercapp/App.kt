package com.bdjr.mercapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.bdjr.mercapp.auth.usecase.ObserveSession
import com.bdjr.mercapp.auth.usecase.EnsureFreshSession
import com.bdjr.mercapp.auth.usecase.RestoreSession
import com.bdjr.mercapp.auth.usecase.SignIn
import com.bdjr.mercapp.auth.usecase.SignOut
import com.bdjr.mercapp.auth.usecase.SignUp
import com.bdjr.mercapp.domain.usecase.ObserveEstablishments
import com.bdjr.mercapp.domain.usecase.ObserveProducts
import com.bdjr.mercapp.domain.usecase.SetProductInShoppingList
import com.bdjr.mercapp.domain.usecase.SoftDeleteEstablishment
import com.bdjr.mercapp.domain.usecase.SoftDeleteProduct
import com.bdjr.mercapp.domain.usecase.UpsertEstablishment
import com.bdjr.mercapp.domain.usecase.UpsertProduct
import com.bdjr.mercapp.sync.usecase.RunSync
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

@Composable
@Preview
fun App() {
    MaterialTheme {
        val koinHolder = remember {
            object : KoinComponent {}
        }
        val koin = remember { koinHolder.getKoin() }

        val observeSession = remember { koin.get<ObserveSession>() }
        val restoreSession = remember { koin.get<RestoreSession>() }
        val ensureFreshSession = remember { koin.get<EnsureFreshSession>() }
        val signIn = remember { koin.get<SignIn>() }
        val signUp = remember { koin.get<SignUp>() }
        val signOut = remember { koin.get<SignOut>() }
        val runSync = remember { koin.get<RunSync>() }

        val session by observeSession().collectAsState()

        LaunchedEffect(Unit) {
            runCatching { restoreSession() }
        }

        LaunchedEffect(session?.userId) {
            if (session != null) {
                runCatching { ensureFreshSession() }
            }
        }

        if (session == null) {
            AuthScreen(
                onSignIn = { email, password -> signIn(email = email, password = password) },
                onSignUp = { email, password -> signUp(email = email, password = password) },
            )
            return@MaterialTheme
        }

        val observeEstablishments = remember { koin.get<ObserveEstablishments>() }
        val upsertEstablishment = remember { koin.get<UpsertEstablishment>() }
        val softDeleteEstablishment = remember { koin.get<SoftDeleteEstablishment>() }
        val observeProducts = remember { koin.get<ObserveProducts>() }
        val upsertProduct = remember { koin.get<UpsertProduct>() }
        val softDeleteProduct = remember { koin.get<SoftDeleteProduct>() }
        val setProductInShoppingList = remember { koin.get<SetProductInShoppingList>() }

        var selectedEstablishmentId by remember { mutableStateOf<String?>(null) }
        var selectedEstablishmentName by remember { mutableStateOf<String?>(null) }

        val products by remember(selectedEstablishmentId) {
            selectedEstablishmentId?.let { id -> observeProducts(establishmentId = id) }
        }?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }

        val establishments by observeEstablishments()
            .collectAsState(initial = emptyList())

        if (selectedEstablishmentId == null) {
            HomeScreen(
                establishmentsCount = establishments.size,
                establishments = establishments,
                onOpen = { est ->
                    selectedEstablishmentId = est.id
                    selectedEstablishmentName = est.name
                },
                onAdd = { name ->
                    val now = System.currentTimeMillis()
                    upsertEstablishment(
                        id = null,
                        name = name,
                        now = now,
                    )
                },
                onEdit = { id, name ->
                    val now = System.currentTimeMillis()
                    upsertEstablishment(
                        id = id,
                        name = name,
                        now = now,
                    )
                },
                onDelete = { id ->
                    val now = System.currentTimeMillis()
                    softDeleteEstablishment(
                        id = id,
                        now = now,
                    )
                },
                onSync = {
                    runSync()
                },
                onSignOut = {
                    signOut()
                },
            )
        } else {
            ProductsScreen(
                establishmentName = selectedEstablishmentName ?: "Productos",
                products = products,
                onBack = {
                    selectedEstablishmentId = null
                    selectedEstablishmentName = null
                },
                onAdd = { name ->
                    val now = System.currentTimeMillis()
                    upsertProduct(
                        id = null,
                        establishmentId = selectedEstablishmentId!!,
                        name = name,
                        isInShoppingList = false,
                        now = now,
                    )
                },
                onEdit = { id, name, isInShoppingList ->
                    val now = System.currentTimeMillis()
                    upsertProduct(
                        id = id,
                        establishmentId = selectedEstablishmentId!!,
                        name = name,
                        isInShoppingList = isInShoppingList,
                        now = now,
                    )
                },
                onToggleShopping = { id, isInShoppingList ->
                    val now = System.currentTimeMillis()
                    setProductInShoppingList(
                        id = id,
                        isInShoppingList = isInShoppingList,
                        now = now,
                    )
                },
                onDelete = { id ->
                    val now = System.currentTimeMillis()
                    softDeleteProduct(
                        id = id,
                        now = now,
                    )
                },
                onSync = {
                    runSync()
                },
                onSignOut = {
                    signOut()
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    establishmentsCount: Int,
    establishments: List<com.bdjr.mercapp.domain.model.Establishment>,
    onOpen: (com.bdjr.mercapp.domain.model.Establishment) -> Unit,
    onAdd: suspend (name: String) -> Unit,
    onEdit: suspend (id: String, name: String) -> Unit,
    onDelete: suspend (id: String) -> Unit,
    onSync: suspend () -> Unit,
    onSignOut: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var addName by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var editName by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<String?>(null) }
    var isSyncing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Mercapp") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (isSyncing) return@launch
                                isSyncing = true
                                runCatching { onSync() }
                                    .onSuccess {
                                        snackbarHostState.showSnackbar("Sincronizado")
                                    }
                                    .onFailure { t ->
                                        snackbarHostState.showSnackbar(t.message ?: "Error al sincronizar")
                                    }
                                isSyncing = false
                            }
                        },
                        enabled = !isSyncing,
                    ) {
                        Text(if (isSyncing) "Sync..." else "Sync")
                    }
                    TextButton(
                        onClick = {
                            scope.launch {
                                onSignOut()
                            }
                        },
                    ) {
                        Text("Salir")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addName = ""
                    showAddDialog = true
                },
            ) {
                Text("+")
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Establecimientos ($establishmentsCount)",
                style = MaterialTheme.typography.titleMedium,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(establishments, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = {
                                            onOpen(item)
                                        },
                                        enabled = !item.isDeleted,
                                    ) {
                                        Text("Abrir")
                                    }
                                    TextButton(
                                        onClick = {
                                            editId = item.id
                                            editName = item.name
                                            showEditDialog = true
                                        },
                                        enabled = !item.isDeleted,
                                    ) {
                                        Text("Editar")
                                    }
                                    TextButton(
                                        onClick = {
                                            deleteId = item.id
                                            showDeleteConfirm = true
                                        },
                                        enabled = !item.isDeleted,
                                    ) {
                                        Text("Eliminar")
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (item.isDeleted) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Eliminado") },
                                    )
                                }
                                if (item.isDirty) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Pendiente Sync") },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (establishments.isEmpty()) {
                Text(
                    text = "Aún no tienes establecimientos. Crea el primero con el +",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nuevo establecimiento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = addName,
                        onValueChange = { addName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = addName.trim()
                        if (name.isEmpty()) return@Button
                        showAddDialog = false
                        scope.launch {
                            runCatching { onAdd(name) }
                                .onFailure { t ->
                                    snackbarHostState.showSnackbar(t.message ?: "Error al guardar")
                                }
                        }
                    },
                    enabled = addName.trim().isNotEmpty(),
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar establecimiento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = editId
                        val name = editName.trim()
                        if (id == null || name.isEmpty()) return@Button
                        showEditDialog = false
                        scope.launch {
                            runCatching { onEdit(id, name) }
                                .onSuccess {
                                    snackbarHostState.showSnackbar("Guardado")
                                }
                                .onFailure { t ->
                                    snackbarHostState.showSnackbar(t.message ?: "Error al guardar")
                                }
                        }
                    },
                    enabled = editId != null && editName.trim().isNotEmpty(),
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar establecimiento") },
            text = { Text("Se marcará como eliminado y se sincronizará en el próximo sync.") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = deleteId
                        if (id == null) return@Button
                        showDeleteConfirm = false
                        scope.launch {
                            runCatching { onDelete(id) }
                                .onSuccess {
                                    snackbarHostState.showSnackbar("Eliminado")
                                }
                                .onFailure { t ->
                                    snackbarHostState.showSnackbar(t.message ?: "Error al eliminar")
                                }
                        }
                    },
                    enabled = deleteId != null,
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(
    establishmentName: String,
    products: List<com.bdjr.mercapp.domain.model.Product>,
    onBack: () -> Unit,
    onAdd: suspend (name: String) -> Unit,
    onEdit: suspend (id: String, name: String, isInShoppingList: Boolean) -> Unit,
    onToggleShopping: suspend (id: String, isInShoppingList: Boolean) -> Unit,
    onDelete: suspend (id: String) -> Unit,
    onSync: suspend () -> Unit,
    onSignOut: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    var tabIndex by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var addName by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var editName by remember { mutableStateOf("") }
    var editInShopping by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<String?>(null) }
    var isSyncing by remember { mutableStateOf(false) }

    val shoppingList = remember(products) {
        products.filter { it.isInShoppingList && !it.isDeleted }
    }

    val filtered = remember(products, query) {
        val tokens = query
            .trim()
            .lowercase()
            .split(" ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (tokens.isEmpty()) {
            products
        } else {
            products.filter { p ->
                val name = p.name.lowercase()
                tokens.all { token -> name.contains(token) }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding(),
        topBar = {
            TopAppBar(
                title = { Text(establishmentName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (isSyncing) return@launch
                                isSyncing = true
                                runCatching { onSync() }
                                    .onSuccess {
                                        snackbarHostState.showSnackbar("Sincronizado")
                                    }
                                    .onFailure { t ->
                                        snackbarHostState.showSnackbar(t.message ?: "Error al sincronizar")
                                    }
                                isSyncing = false
                            }
                        },
                        enabled = !isSyncing,
                    ) {
                        Text(if (isSyncing) "Sync..." else "Sync")
                    }
                    TextButton(
                        onClick = {
                            scope.launch { onSignOut() }
                        },
                    ) {
                        Text("Salir")
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Atrás")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addName = ""
                    showAddDialog = true
                },
            ) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = { Text("Productos") },
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = { Text("Lista (${shoppingList.size})") },
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            if (tabIndex == 0) {
                Text(
                    text = "Productos (${filtered.size})",
                    style = MaterialTheme.typography.titleMedium,
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !item.isDeleted) {
                                    val next = !item.isInShoppingList
                                    scope.launch {
                                        runCatching { onToggleShopping(item.id, next) }
                                            .onFailure { t ->
                                                snackbarHostState.showSnackbar(t.message ?: "Error")
                                            }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TextButton(
                                            onClick = {
                                                val next = !item.isInShoppingList
                                                scope.launch {
                                                    runCatching { onToggleShopping(item.id, next) }
                                                        .onFailure { t ->
                                                            snackbarHostState.showSnackbar(t.message ?: "Error")
                                                        }
                                                }
                                            },
                                            enabled = !item.isDeleted,
                                        ) {
                                            Text(if (item.isInShoppingList) "Quitar" else "Agregar")
                                        }
                                        TextButton(
                                            onClick = {
                                                editId = item.id
                                                editName = item.name
                                                editInShopping = item.isInShoppingList
                                                showEditDialog = true
                                            },
                                            enabled = !item.isDeleted,
                                        ) {
                                            Text("Editar")
                                        }
                                        TextButton(
                                            onClick = {
                                                deleteId = item.id
                                                showDeleteConfirm = true
                                            },
                                            enabled = !item.isDeleted,
                                        ) {
                                            Text("Eliminar")
                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (item.isInShoppingList) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("En lista") },
                                        )
                                    }
                                    if (item.isDeleted) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("Eliminado") },
                                        )
                                    }
                                    if (item.isDirty) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("Pendiente Sync") },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (products.isEmpty()) {
                    Text(
                        text = "Aún no tienes productos. Crea el primero con el +",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Text(
                    text = "Lista de mercado (${shoppingList.size})",
                    style = MaterialTheme.typography.titleMedium,
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(shoppingList, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        runCatching { onToggleShopping(item.id, false) }
                                            .onFailure { t ->
                                                snackbarHostState.showSnackbar(t.message ?: "Error")
                                            }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "Quitar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }

                if (shoppingList.isEmpty()) {
                    Text(
                        text = "Tu lista está vacía. Vuelve a Productos y toca para agregar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nuevo producto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = addName,
                        onValueChange = { addName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = addName.trim()
                        if (name.isEmpty()) return@Button
                        showAddDialog = false
                        scope.launch {
                            runCatching { onAdd(name) }
                                .onSuccess {
                                    snackbarHostState.showSnackbar("Guardado")
                                }
                                .onFailure { t ->
                                    snackbarHostState.showSnackbar(t.message ?: "Error al guardar")
                                }
                        }
                    },
                    enabled = addName.trim().isNotEmpty(),
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar producto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = editId
                        val name = editName.trim()
                        if (id == null || name.isEmpty()) return@Button
                        showEditDialog = false
                        scope.launch {
                            runCatching { onEdit(id, name, editInShopping) }
                                .onSuccess {
                                    snackbarHostState.showSnackbar("Guardado")
                                }
                                .onFailure { t ->
                                    snackbarHostState.showSnackbar(t.message ?: "Error al guardar")
                                }
                        }
                    },
                    enabled = editId != null && editName.trim().isNotEmpty(),
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar producto") },
            text = { Text("Se marcará como eliminado y se sincronizará en el próximo sync.") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = deleteId
                        if (id == null) return@Button
                        showDeleteConfirm = false
                        scope.launch {
                            runCatching { onDelete(id) }
                                .onSuccess {
                                    snackbarHostState.showSnackbar("Eliminado")
                                }
                                .onFailure { t ->
                                    snackbarHostState.showSnackbar(t.message ?: "Error al eliminar")
                                }
                        }
                    },
                    enabled = deleteId != null,
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun AuthScreen(
    onSignIn: suspend (email: String, password: String) -> Unit,
    onSignUp: suspend (email: String, password: String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isSignUp) "Crea tu cuenta" else "Bienvenido",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = if (isSignUp) "Regístrate para empezar" else "Inicia sesión para sincronizar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.padding(top = 4.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            if (error != null) {
                Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (isLoading) return@Button
                    error = null
                    isLoading = true
                    scope.launch {
                        runCatching {
                            if (isSignUp) {
                                onSignUp(email.trim(), password)
                            } else {
                                onSignIn(email.trim(), password)
                            }
                        }.onSuccess {
                            snackbarHostState.showSnackbar(
                                if (isSignUp) "Cuenta creada" else "Sesión iniciada",
                            )
                        }.onFailure { t ->
                            error = t.message ?: "Error"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
            ) {
                Text(text = if (isLoading) "Procesando..." else if (isSignUp) "Crear cuenta" else "Entrar")
            }

            TextButton(
                onClick = {
                    error = null
                    isSignUp = !isSignUp
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = if (isSignUp) "Ya tengo cuenta" else "Quiero registrarme")
            }
        }
    }
}