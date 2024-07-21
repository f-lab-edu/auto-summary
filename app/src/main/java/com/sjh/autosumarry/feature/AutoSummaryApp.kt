package com.sjh.autosumarry.feature

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sjh.autosumarry.feature.history.navigation.historyScreen
import com.sjh.autosumarry.feature.history.navigation.navigateToHistory
import com.sjh.autosumarry.feature.main.navigation.MAIN_ROUTE
import com.sjh.autosumarry.feature.main.navigation.mainScreen
import com.sjh.autosumarry.feature.summary.navigation.navigateToSummary
import com.sjh.autosumarry.feature.summary.navigation.summaryScreen

@Composable
fun AutoSummaryApp() {
    AutoSummaryNavHost(
        modifier = Modifier,
    )
}

@Composable
fun AutoSummaryNavHost(
    modifier: Modifier,
    startDestination: String = MAIN_ROUTE,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        mainScreen(onHistoryClick = navController::navigateToHistory)
        historyScreen(onSummaryClick = navController::navigateToSummary)
        summaryScreen(onBackClick = navController::popBackStack)
    }
}
