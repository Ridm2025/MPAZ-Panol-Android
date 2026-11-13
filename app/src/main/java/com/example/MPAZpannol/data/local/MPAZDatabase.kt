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
        EquipoEntity::class,
        ReservaEntity::class,
        ReservaDetalleEntity::class
    ],
    version = 3,
    exportSchema = false
)

abstract class MPAZDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun catalogoDao(): CatalogoDao

    abstract fun reservaDao(): ReservaDao

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

        private val MIGRATION_2_3 =
            object : Migration(2, 3) {

                override fun migrate(
                    connection: SQLiteConnection
                ) {

                    connection.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `reservas` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `usuarioId` INTEGER NOT NULL,
                    `sala` TEXT NOT NULL,
                    `inicioMillis` INTEGER NOT NULL,
                    `finMillis` INTEGER NOT NULL,
                    `estado` TEXT NOT NULL,
                    `creadaMillis` INTEGER NOT NULL,
                    FOREIGN KEY(`usuarioId`)
                    REFERENCES `usuarios`(`id`)
                    ON UPDATE NO ACTION
                    ON DELETE RESTRICT
                )
                """.trimIndent()
                    )

                    connection.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reservas_usuarioId`
                ON `reservas` (`usuarioId`)
                """.trimIndent()
                    )

                    connection.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `reserva_detalles` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `reservaId` INTEGER NOT NULL,
                    `equipoId` INTEGER NOT NULL,
                    `cantidad` INTEGER NOT NULL,
                    FOREIGN KEY(`reservaId`)
                    REFERENCES `reservas`(`id`)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE,
                    FOREIGN KEY(`equipoId`)
                    REFERENCES `equipos`(`id`)
                    ON UPDATE NO ACTION
                    ON DELETE RESTRICT
                )
                """.trimIndent()
                    )

                    connection.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reserva_detalles_reservaId`
                ON `reserva_detalles` (`reservaId`)
                """.trimIndent()
                    )

                    connection.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reserva_detalles_equipoId`
                ON `reserva_detalles` (`equipoId`)
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
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
