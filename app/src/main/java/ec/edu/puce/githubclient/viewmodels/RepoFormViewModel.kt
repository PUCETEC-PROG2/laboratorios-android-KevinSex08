package ec.edu.puce.githubclient.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.githubclient.models.RepositoryPayload
import ec.edu.puce.githubclient.services.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepoFormViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    private val _inSuccess = MutableStateFlow(false)
    val inSuccess: StateFlow<Boolean> = _inSuccess.asStateFlow()

    fun createRepo(name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                val repoBody = RepositoryPayload(name, description)
                RetrofitClient.apiService.createRepository(repoBody)
                _inSuccess.value = true
            } catch (e: Exception) {
                _errorMsg.value = "Error al crear: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateRepo(repoIdString: String, newName: String, newDescription: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                val parts = repoIdString.split("/")


                val owner = if (parts.size > 1) parts[0] else "me"
                val actualRepoName = if (parts.size > 1) parts[1] else repoIdString

                val repoBody = RepositoryPayload(newName, newDescription)


                val response = RetrofitClient.apiService.updateRepository(
                    owner = owner,
                    repoName = actualRepoName,
                    repo = repoBody
                )


                if (response.isSuccessful) {
                    _inSuccess.value = true
                } else {
                    _errorMsg.value = "Error al editar: Verifique permisos del token (Código ${response.code()})"
                }
            } catch (e: Exception) {
                _errorMsg.value = "Error de red al editar: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSuccess() {
        _inSuccess.value = false
    }

    fun resetError() {
        _errorMsg.value = null
    }
}