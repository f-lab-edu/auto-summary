package com.sjh.autosumarry.feature.history.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sjh.autosumarry.feature.history.HistoryRoute

const val HISTORY_ROUTE = "/history"

fun NavController.navigateToHistory() = navigate(HISTORY_ROUTE)

fun NavGraphBuilder.historyScreen(
    onSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = HISTORY_ROUTE) {
        HistoryRoute(onSummaryClick)
    }
}
