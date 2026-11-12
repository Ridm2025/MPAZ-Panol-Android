package com.example.MPAZpannol.data

import com.example.MPAZpannol.data.local.PasswordHasher
import com.example.MPAZpannol.data.local.UsuarioDao
import com.example.MPAZpannol.data.local.UsuarioEntity

enum class ResultadoRegistro {
    EXITO,
    CORREO_DUPLICADO
}

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String
    ): ResultadoRegistro {

        val usuarioExistente =
            usuarioDao.buscarPorCorreo(correo)

        if (usuarioExistente != null) {
            return ResultadoRegistro.CORREO_DUPLICADO
        }

        val passwordProtegida =
            PasswordHasher.crear(password)

        val nuevoUsuario = UsuarioEntity(
            nombre = nombre,
            correo = correo,
            passwordHash = passwordProtegida.hash,
            passwordSalt = passwordProtegida.salt,
            rol = "SOLICITANTE"
        )

        usuarioDao.insertar(nuevoUsuario)

        return ResultadoRegistro.EXITO
    }
}
