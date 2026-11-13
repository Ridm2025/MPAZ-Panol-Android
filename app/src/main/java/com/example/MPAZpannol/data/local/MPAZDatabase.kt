package com.example.MPAZpannol.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

@Database(
    entities = [
        UsuarioEntity::class,
        CategoriaEntity::class,
        EquipoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MPAZDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun catalogoDao(): CatalogoDao

    companion object {

        @Volatile
        private var INSTANCE: MPAZDatabase? = null

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    connection: SQLiteConnection
                ) {

                    connection.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `categorias` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `nombre` TEXT NOT NULL
                        )
                        """.trimIndent()
                    )

                    connection.execSQL(
                        """
                        CREATE UNIQUE INDEX IF NOT EXISTS
                        `index_categorias_nombre`
                        ON `categorias` (`nombre`)
                        """.trimIndent()
                    )

                    connection.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `equipos` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `nombre` TEXT NOT NULL,
                            `categoriaId` INTEGER NOT NULL,
                            `descripcion` TEXT NOT NULL,
                            `stockTotal` INTEGER NOT NULL,
                            `stockDisponible` INTEGER NOT NULL,
                            `activo` INTEGER NOT NULL,
                            FOREIGN KEY(`categoriaId`)
                            REFERENCES `categorias`(`id`)
                            ON UPDATE NO ACTION
                            ON DELETE RESTRICT
                        )
                        """.trimIndent()
                    )

                    connection.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_equipos_categoriaId`
                        ON `equipos` (`categoriaId`)
                        """.trimIndent()
                    )
                }
            }

        fun getInstance(context: Context): MPAZDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MPAZDatabase::class.java,
                    "mpaz_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
