package com.example.fakestoreexam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fakestoreexam.ui.screens.DetailScreen
import com.example.fakestoreexam.ui.screens.HomeScreen
import com.example.fakestoreexam.viewmodel.ProductViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = productViewModel,
                onProductClick = { productId ->
                    navController.navigate("detail/$productId")
                }
            )
        }

        composable(
            route = "detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            DetailScreen(
                productId = productId,
                viewModel = productViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
