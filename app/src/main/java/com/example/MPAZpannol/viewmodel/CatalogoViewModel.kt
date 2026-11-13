package com.example.MPAZpannol.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.CatalogoRepository
import com.example.MPAZpannol.data.local.CategoriaEntity
import com.example.MPAZpannol.data.local.EquipoEntity
import com.example.MPAZpannol.data.local.MPAZDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = CatalogoRepository(
        MPAZDatabase.getInstance(application).catalogoDao()
    )

    var categorias by mutableStateOf<List<CategoriaEntity>>(
        emptyList()
    )
        private set

    var equipos by mutableStateOf<List<EquipoEntity>>(
        emptyList()
    )
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        cargarCatalogo()
    }

    private fun cargarCatalogo() {

        viewModelScope.launch {

            try {
                categorias = withContext(Dispatchers.IO) {
                    repository.cargarDatosDemoSiVacio()
                    repository.obtenerCategorias()
                }

                repository.observarEquipos().collect {
                    equipos = it
                    isLoading = false
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                error = "No se pudo cargar el catálogo"
                isLoading = false
            }
        }
    }
}
