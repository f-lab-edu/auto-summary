package com.sjh.autosummary.feature.main.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sjh.autosummary.feature.main.MainRoute

const val MAIN_ROUTE = "/main"
const val HISTORYDATA_KEY = "messagelist"

fun NavController.navigateToMain(data: Long) {
    navigate("$MAIN_ROUTE/$data")
}

fun NavGraphBuilder.mainScreen(
    onHistoryClick: () -> Unit,
    dataKey: String,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "$MAIN_ROUTE/{$HISTORYDATA_KEY}",
        arguments = listOf(navArgument(dataKey) { type = NavType.LongType }),
    ) { backStackEntry ->
        val chatHistoryId = backStackEntry.arguments?.getLong(dataKey)
        MainRoute(
            onHistoryClick = onHistoryClick,
            chatHistoryId = chatHistoryId,
            modifier = modifier,
        )
    }
}
