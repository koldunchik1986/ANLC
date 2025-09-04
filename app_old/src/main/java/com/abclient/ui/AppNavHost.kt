package com.abclient.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abclient.ui.settings.SettingsScreen
import com.abclient.ui.settings.AuthAdaptationSettingsScreen
import com.abclient.ui.profile.AddProfileScreen
import com.abclient.ui.welcome.WelcomeScreen
import com.abclient.profile.UserProfile

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "welcome", modifier = modifier) {
        composable("welcome") {
            WelcomeScreen(
                onSettingsClick = { navController.navigate("settings") },
                onAddProfileClick = { navController.navigate("add_profile") },
                onLoginProfile = { profile ->
                    // Здесь можно реализовать авторизацию профиля и переход к экрану игры
                    navController.navigate("game/${profile.login}")
                }
            )
        }
        composable("settings") {
            SettingsScreen(onAdaptationClick = { navController.navigate("auth_adaptation") })
        }
        composable("auth_adaptation") {
            AuthAdaptationSettingsScreen(
                context = context,
                configName = "auth_config.json",
                onConfigChanged = { /* handle config change if needed */ }
            )
        }
        composable("add_profile") {
            AddProfileScreen(
                context = context,
                configName = "auth_config.json",
                onProfileAdded = { navController.popBackStack() },
                onAdaptationRequested = { navController.navigate("auth_adaptation") }
            )
        }
        composable("game/{login}") { backStackEntry ->
            val login = backStackEntry.arguments?.getString("login") ?: ""
            GameScreen(login)
        }
    }
}

@Composable
fun GameScreen(login: String) {
    Column(Modifier.padding(16.dp)) {
        Text("Вход выполнен: $login", style = MaterialTheme.typography.titleLarge)
        // TODO: реализовать основной игровой UI
    }
}
