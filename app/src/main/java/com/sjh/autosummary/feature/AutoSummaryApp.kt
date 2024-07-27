package com.sjh.autosummary.feature

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sjh.autosummary.feature.history.navigation.historyScreen
import com.sjh.autosummary.feature.history.navigation.navigateToHistory
import com.sjh.autosummary.feature.main.navigation.HISTORYDATA_KEY
import com.sjh.autosummary.feature.main.navigation.MAIN_ROUTE
import com.sjh.autosummary.feature.main.navigation.mainScreen
import com.sjh.autosummary.feature.main.navigation.navigateToMain
import com.sjh.autosummary.feature.summary.navigation.navigateToSummary
import com.sjh.autosummary.feature.summary.navigation.summaryScreen

@Composable
fun AutoSummaryApp() {
    AutoSummaryNavHost(
        modifier = Modifier,
    )
}

@Composable
fun AutoSummaryNavHost(
    modifier: Modifier,
    startDestination: String = "$MAIN_ROUTE/{$HISTORYDATA_KEY}",
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        mainScreen(onHistoryClick = navController::navigateToHistory, dataKey = HISTORYDATA_KEY)
        historyScreen(
            onChatHistoryClick = { messageList -> navController.navigateToMain(messageList) },
            onSummaryClick = navController::navigateToSummary,
        )
        summaryScreen(onBackClick = navController::popBackStack)
    }
}
