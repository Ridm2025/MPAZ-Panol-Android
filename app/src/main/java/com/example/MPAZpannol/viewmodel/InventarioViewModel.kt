package com.example.MPAZpannol.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.CatalogoRepository
import com.example.MPAZpannol.data.local.InventarioResumen
import com.example.MPAZpannol.data.local.MPAZDatabase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class InventarioUiState(
    val equipos: List<InventarioResumen> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class InventarioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = CatalogoRepository(
        MPAZDatabase.getInstance(application)
            .catalogoDao()
    )

    var uiState by mutableStateOf(InventarioUiState())
        private set

    init {
        viewModelScope.launch {

            repository.observarResumenInventario()
                .catch {
                    uiState = InventarioUiState(
                        isLoading = false,
                        error = "No se pudo cargar el inventario"
                    )
                }
                .collect { equipos ->
                    uiState = InventarioUiState(
                        equipos = equipos,
                        isLoading = false
                    )
                }
        }
    }
}
