package com.example.MPAZpannol.navigation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.MPAZpannol.ui.screens.WelcomeScreen
import com.example.MPAZpannol.ui.screens.LoginScreen
import com.example.MPAZpannol.ui.screens.RegisterScreen
import com.example.MPAZpannol.ui.screens.HomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    var nombreUsuario by rememberSaveable {
        mutableStateOf("")
    }

    var rolUsuario by rememberSaveable {
        mutableStateOf("")
    }

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        composable("welcome") {

            WelcomeScreen(
                onLoginClick = {
                    navController.navigate("login")
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {

            RegisterScreen(
                onRegisterSuccess = {

                    Toast.makeText(
                        context,
                        "Cuenta registrada correctamente",
                        Toast.LENGTH_LONG
                    ).show()

                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("login") {

            LoginScreen(
                onLoginSuccess = { nombre, rol ->

                    nombreUsuario = nombre
                    rolUsuario = rol

                    navController.navigate("home") {
                        popUpTo("welcome") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {

            HomeScreen(
                nombreUsuario = nombreUsuario,
                rolUsuario = rolUsuario,
                onLogout = {

                    nombreUsuario = ""
                    rolUsuario = ""

                    navController.navigate("welcome") {
                        popUpTo("home") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
