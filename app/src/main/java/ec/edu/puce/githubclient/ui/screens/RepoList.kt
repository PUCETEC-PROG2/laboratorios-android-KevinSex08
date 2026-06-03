package ec.edu.puce.githubclient.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ec.edu.puce.githubclient.models.Repository
import ec.edu.puce.githubclient.ui.components.RepoItem
import ec.edu.puce.githubclient.viewmodels.RepoListViewModel
import kotlinx.coroutines.launch

@Composable
fun RepoList(
    modifier: Modifier = Modifier,
    viewModel: RepoListViewModel,
    onNavigateToForm: (String?) -> Unit
) {
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()

    // Estados para las alertas
    var repoToDelete by remember { mutableStateOf<Repository?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialogo de Confirmación para Eliminar
    if (repoToDelete != null) {
        AlertDialog(
            onDismissRequest = { repoToDelete = null },
            title = { Text("¿Eliminar repositorio?") },
            text = { Text("¿Estás seguro de que deseas eliminar '${repoToDelete?.name}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        val repoName = repoToDelete?.name ?: ""
                        repoToDelete?.let { viewModel.deleteRepo(it) }
                        repoToDelete = null
                        // Mostrar mensaje de éxito
                        scope.launch {
                            snackbarHostState.showSnackbar("Repositorio '$repoName' eliminado correctamente")
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { repoToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { innerPadding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }

            if (!isLoading && errorMsg.isNullOrBlank()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(repos) { repo: Repository ->
                        RepoItem(
                            repository = repo,
                            onEdit = { id -> onNavigateToForm(id) },
                            onDelete = {
                                repoToDelete = repo
                            }
                        )
                    }
                }
            }
        }
    }
}