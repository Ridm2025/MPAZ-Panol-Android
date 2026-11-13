package com.example.MPAZpannol.data

import com.example.MPAZpannol.data.local.CatalogoDao
import com.example.MPAZpannol.data.local.CategoriaEntity
import com.example.MPAZpannol.data.local.EquipoEntity
import kotlinx.coroutines.flow.Flow

class CatalogoRepository(
    private val catalogoDao: CatalogoDao
) {

    fun observarEquipos(): Flow<List<EquipoEntity>> {
        return catalogoDao.observarEquipos()
    }

    suspend fun obtenerCategorias(): List<CategoriaEntity> {
        return catalogoDao.obtenerCategorias()
    }

    suspend fun cargarDatosDemoSiVacio() {

        if (catalogoDao.obtenerCategorias().isNotEmpty()) {
            return
        }

        val tecnologia = catalogoDao.insertarCategoria(
            CategoriaEntity(nombre = "Tecnología")
        )

        val audiovisual = catalogoDao.insertarCategoria(
            CategoriaEntity(nombre = "Audiovisual")
        )

        val redes = catalogoDao.insertarCategoria(
            CategoriaEntity(nombre = "Redes")
        )

        val robotica = catalogoDao.insertarCategoria(
            CategoriaEntity(nombre = "Robótica")
        )

        catalogoDao.insertarEquipo(
            EquipoEntity(
                nombre = "Notebook",
                categoriaId = tecnologia,
                descripcion = "Computador portátil para uso académico",
                stockTotal = 10,
                stockDisponible = 8
            )
        )

        catalogoDao.insertarEquipo(
            EquipoEntity(
                nombre = "Tablet",
                categoriaId = tecnologia,
                descripcion = "Dispositivo táctil para actividades educativas",
                stockTotal = 6,
                stockDisponible = 6
            )
        )

        catalogoDao.insertarEquipo(
            EquipoEntity(
                nombre = "Proyector",
                categoriaId = audiovisual,
                descripcion = "Proyector multimedia para salas de clases",
                stockTotal = 5,
                stockDisponible = 3
            )
        )

        catalogoDao.insertarEquipo(
            EquipoEntity(
                nombre = "Router WiFi",
                categoriaId = redes,
                descripcion = "Router para prácticas de redes",
                stockTotal = 4,
                stockDisponible = 4
            )
        )

        catalogoDao.insertarEquipo(
            EquipoEntity(
                nombre = "Kit LEGO Education",
                categoriaId = robotica,
                descripcion = "Kit educativo de robótica",
                stockTotal = 3,
                stockDisponible = 2
            )
        )
    }
}
