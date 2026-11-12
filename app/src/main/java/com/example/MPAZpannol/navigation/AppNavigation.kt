package com.example.MPAZpannol.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.MPAZpannol.ui.screens.WelcomeScreen
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.MPAZpannol.ui.screens.LoginScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

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

        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    Toast.makeText(
                        context,
                        "Validaciones del login: siguiente paso",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("register") {
            PantallaTemporal(
                titulo = "Registrarse",
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun PantallaTemporal(
    titulo: String,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onVolver) {
            Text("Volver")
        }
    }
}
