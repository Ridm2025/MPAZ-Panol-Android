package com.example.MPAZpannol.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UsuarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MPAZDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var INSTANCE: MPAZDatabase? = null

        fun getInstance(context: Context): MPAZDatabase {
            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MPAZDatabase::class.java,
                    "mpaz_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
