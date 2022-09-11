package io.prashant.topalbums.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.prashant.topalbums.ui.screen.AlbumScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = AppScreen.ALBUM
    ) {

        composable(route = AppScreen.ALBUM) {
            AlbumScreen(navController, hiltViewModel())
        }


    }
}