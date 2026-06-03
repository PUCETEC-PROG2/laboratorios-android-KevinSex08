package ec.edu.puce.githubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.puce.githubclient.ui.screens.RepoForm
import ec.edu.puce.githubclient.ui.screens.RepoList
import ec.edu.puce.githubclient.ui.theme.GithubClientTheme
import ec.edu.puce.githubclient.viewmodels.RepoListViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GithubClientTheme {
                // Navegación manual por estados
                var currentScreen by remember { mutableStateOf("repoList") }

                // Esta variable guardará el ID cuando presionemos "Editar"
                var selectedRepoId by remember { mutableStateOf<String?>(null) }

                val listViewModel: RepoListViewModel = viewModel()

                when (currentScreen) {
                    "repoList" -> RepoList(
                        viewModel = listViewModel,
                        onNavigateToForm = { id ->
                            selectedRepoId = id // Guardamos el ID (será null si es un nuevo repo)
                            currentScreen = "repoForm"
                        }
                    )

                    "repoForm" -> RepoForm(
                        repoId = selectedRepoId,
                        onBackClick = {
                            selectedRepoId = null
                            currentScreen = "repoList"
                        },
                        onSaveSuccess = {
                            // 1. Primero cambiamos de pantalla para que la UI reaccione
                            currentScreen = "repoList"
                            // 2. Limpiamos la variable
                            selectedRepoId = null
                            // 3. Finalmente, pedimos al servidor que recargue los datos
                            listViewModel.fetchRepos()
                        }
                    )
                }
            }
        }
    }
}