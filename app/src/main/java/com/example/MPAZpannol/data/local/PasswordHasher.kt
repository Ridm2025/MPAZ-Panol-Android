package com.example.MPAZpannol.data.local

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class PasswordHash(
    val hash: String,
    val salt: String
)

object PasswordHasher {

    private const val ITERATIONS = 210_000
    private const val KEY_LENGTH = 256

    fun crear(password: String): PasswordHash {
        val saltBytes = ByteArray(16)

        SecureRandom().nextBytes(saltBytes)

        val hashBytes = derivar(password, saltBytes)

        return PasswordHash(
            hash = Base64.encodeToString(
                hashBytes,
                Base64.NO_WRAP
            ),
            salt = Base64.encodeToString(
                saltBytes,
                Base64.NO_WRAP
            )
        )
    }

    fun verificar(
        password: String,
        hashGuardado: String,
        saltGuardado: String
    ): Boolean {
        val saltBytes = Base64.decode(
            saltGuardado,
            Base64.NO_WRAP
        )

        val hashOriginal = Base64.decode(
            hashGuardado,
            Base64.NO_WRAP
        )

        val hashIngresado = derivar(password, saltBytes)

        return MessageDigest.isEqual(
            hashOriginal,
            hashIngresado
        )
    }

    private fun derivar(
        password: String,
        salt: ByteArray
    ): ByteArray {
        val caracteres = password.toCharArray()

        val spec = PBEKeySpec(
            caracteres,
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {
            SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            ).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
            caracteres.fill('\u0000')
        }
    }
}
