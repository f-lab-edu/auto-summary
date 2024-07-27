package com.sjh.autosummary.feature.main.navigation

import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sjh.autosummary.core.model.ChatMessage
import com.sjh.autosummary.feature.main.MainRoute
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

const val MAIN_ROUTE = "/main"
const val HISTORYDATA_KEY = "messagelist"

fun NavController.navigateToMain(data: List<ChatMessage>) {
    val json = Uri.encode(Json.encodeToString(ListSerializer(ChatMessage.serializer()), data))
    this.navigate("$MAIN_ROUTE/$json")
}

fun NavGraphBuilder.mainScreen(
    onHistoryClick: () -> Unit,
    dataKey: String,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "$MAIN_ROUTE/{$HISTORYDATA_KEY}",
        arguments = listOf(navArgument(dataKey) { type = NavType.StringType }),
    ) { backStackEntry ->
        val json = backStackEntry.arguments?.getString(dataKey)
        val chatList =
            json?.let { Json.decodeFromString(ListSerializer(ChatMessage.serializer()), it) }
        MainRoute(
            onHistoryClick = onHistoryClick,
            messageList = chatList ?: emptyList(),
            modifier = modifier,
        )
    }
}
