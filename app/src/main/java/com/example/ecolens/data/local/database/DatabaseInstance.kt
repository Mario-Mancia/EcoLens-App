package com.example.ecolens.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ecolens.data.local.dao.*
import com.example.ecolens.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/*
object DatabaseInstance {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ecolens_database"
            )
                .addCallback(SeedDatabaseCallback(context))
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }

    private class SeedDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    // Insertar usuario admin
                    database.userDao().insertUser(
                        UserEntity(
                            username = "admin",
                            email = "admin123@gmail.com",
                            country = "El Salvador",
                            gender = "M",
                            password = "admin123"
                        )
                    )

                    // Insertar logros predeterminados
                    val defaultAchievements = listOf(
                        AchievementsEntity(1,"Reciclador principiante", "Clasifica tu primer residuo con EcoLens.", "reciclaje"),
                        AchievementsEntity(2,"Maestro del plástico", "Has clasificado un total de 50 ítems de plástico.", "reciclaje"),
                        AchievementsEntity(3, "Héroe del vidrio", "Tu compromiso con el vidrio es cristalino.", "reciclaje"),
                        AchievementsEntity(4, "Caminante urbano", "Has dado tus primeros 1,000 pasos Eco.", "pasos"),
                        AchievementsEntity(5, "Activista del reciclaje", "Alcanza los 500 Puntos Eco.", "reciclaje"),
                        AchievementsEntity(6, "Explorador sostenible", "Has escaneado tu primer código QR de producto/punto sostenible.", "qr"),
                        AchievementsEntity(7, "Consumidor consciente", "Has escaneado 5 códigos QR de productos sostenibles diferentes.", "qr"),
                        AchievementsEntity(8, "Pionero EcoLens", "Uno de los primeros 10 usuarios en unirse a la comunidad EcoLens.", "otros"),
                        AchievementsEntity(9, "EcoUsuario activo", "Abre la app durante 3 días consecutivos.", "otros"),
                        AchievementsEntity(10, "Primer paso Eco", "Registra tu primer paso con el sensor.", "pasos")
                    )

                    database.archievementsDao().insertAllAchievements(defaultAchievements)
                }
            }
        }
    }
}
*/



object DatabaseInstance {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ecolens_database"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // Agregar datos por defecto
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                database.userDao().insertUser(
                                    UserEntity(
                                        username = "admin",
                                        email = "admin123@gmail.com",
                                        country = "El Salvador",
                                        gender = "M",
                                        password = "admin123"
                                    )
                                )
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }
}