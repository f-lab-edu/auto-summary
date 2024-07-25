package com.sjh.autosummary.feature.main.navigation

import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sjh.autosummary.core.model.ChatMessage
import com.sjh.autosummary.feature.history.navigation.navigateToHistory
import com.sjh.autosummary.feature.main.MainRoute
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

const val MAIN_ROUTE = "/main"
const val DATA_KEY = "messagelist"

fun NavController.navigateToMain(data: List<ChatMessage>) {
    val json = Uri.encode(Json.encodeToString(ListSerializer(ChatMessage.serializer()), data))
    this.navigate("$MAIN_ROUTE/$json")
}

fun NavGraphBuilder.mainScreen(
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
    dataKey: String? = null,
) {
    if (dataKey != null) {
        composable(
            route = "$MAIN_ROUTE/{$dataKey}",
            arguments = listOf(navArgument(dataKey) { type = NavType.StringType }),
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString(dataKey)
            val chatList =
                json?.let { Json.decodeFromString(ListSerializer(ChatMessage.serializer()), it) }
            MainRoute(
                onHistoryClick = onHistoryClick,
                messageList = chatList ?: emptyList(),
                modifier = modifier
            )
        }
    } else {
        composable(route = MAIN_ROUTE) {
            MainRoute(
                onHistoryClick = onHistoryClick,
                messageList = emptyList(), // 기본값 설정
                modifier = modifier
            )
        }
    }

}
