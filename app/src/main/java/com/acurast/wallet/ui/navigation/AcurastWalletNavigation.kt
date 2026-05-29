package com.acurast.wallet.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.acurast.wallet.ui.screens.home.HomeScreen
import com.acurast.wallet.ui.screens.create.CreateWalletScreen
import com.acurast.wallet.ui.screens.transfer.TransferScreen
import com.acurast.wallet.ui.screens.history.HistoryScreen
import com.acurast.wallet.ui.screens.settings.SettingsScreen

/**
 * 导航路由定义
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "主页", Icons.Default.Home)
    object Create : Screen("create", "创建钱包", Icons.Default.Add)
    object Transfer : Screen("transfer", "转账", Icons.Default.Send)
    object History : Screen("history", "交易历史", Icons.Default.List)
    object Settings : Screen("settings", "设置", Icons.Default.Settings)
}

/**
 * Acurast Wallet 主导航
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcurastWalletNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 底部导航栏项目
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Transfer,
        Screen.History,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(64.dp)
            ) {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToCreate = { navController.navigate(Screen.Create.route) },
                    onNavigateToTransfer = { navController.navigate(Screen.Transfer.route) }
                )
            }
            composable(Screen.Create.route) {
                CreateWalletScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onWalletCreated = { navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    } }
                )
            }
            composable(Screen.Transfer.route) {
                TransferScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
