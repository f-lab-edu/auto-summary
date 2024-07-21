package com.sjh.autosumarry.feature.main.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sjh.autosumarry.feature.main.MainRoute

const val MAIN_ROUTE = "/main"

fun NavController.navigateToMain() = navigate(MAIN_ROUTE)
fun NavGraphBuilder.mainScreen(
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = MAIN_ROUTE) {
        MainRoute(onHistoryClick)
    }
}
