package com.sjh.autosummary.feature.history.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.core.model.ChatMessage
import com.sjh.autosummary.feature.history.HistoryRoute

const val HISTORY_ROUTE = "/history"

fun NavController.navigateToHistory() = navigate(HISTORY_ROUTE)

fun NavGraphBuilder.historyScreen(
    onChatHistoryClick: (List<ChatMessage>) -> Unit,
    onSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = HISTORY_ROUTE) {
        HistoryRoute(
            onChatHistoryClick = onChatHistoryClick,
            onSummaryClick = onSummaryClick,
            modifier = modifier
        )
    }
}
