package com.sjh.autosummary.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjh.autosummary.R
import com.sjh.autosummary.core.designsystem.theme.AutoSummaryTheme
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.feature.main.MainViewModel

@Composable
fun HistoryRoute(
    onChatHistoryClick: (Long) -> Unit,
    onSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    HistoryScreen(
        onChatHistoryClick = onChatHistoryClick,
        onSummaryClick = onSummaryClick,
        // Todo : State로 변경
        chatHistoryList = listOf(),
        modifier = modifier,
    )
}

@Composable
fun HistoryScreen(
    onChatHistoryClick: (Long) -> Unit,
    onSummaryClick: () -> Unit,
    chatHistoryList: List<ChatHistory>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            HistoryTopBar(onSummaryClick = onSummaryClick)
        },
    ) { padding ->
        Box(
            modifier = modifier.padding(padding),
        ) {
            HistroyContent(
                onChatHistoryClick = onChatHistoryClick,
                chatHistoryList = chatHistoryList,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(
    onSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text("History")
        },
        modifier = modifier.statusBarsPadding(),
        actions = {
            IconButton(onClick = onSummaryClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_summarize),
                    contentDescription = "Summary",
                )
            }
        },
    )
}

@Composable
fun HistroyContent(
    onChatHistoryClick: (Long) -> Unit,
    chatHistoryList: List<ChatHistory>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        LazyColumn(
            modifier =
            modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            itemsIndexed(chatHistoryList) { idx, chat ->
                ChatHistoryItem(
                    onChatHistoryClick = onChatHistoryClick,
                    chat = chat,
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
fun ChatHistoryItem(
    onChatHistoryClick: (Long) -> Unit,
    chat: ChatHistory,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                chat.id?.let(onChatHistoryClick)
            },
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = chat.date,
                fontSize = 16.sp,
            )

            Text(
                modifier = Modifier.wrapContentSize(),
                text = chat.name,
                fontSize = 16.sp,
            )
        }

        Spacer(
            modifier =
            modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color.DarkGray),
        )
    }
}

@Preview
@Composable
private fun HistoryScreenPreview() {
    AutoSummaryTheme {
        HistoryScreen(
            onChatHistoryClick = { a -> },
            onSummaryClick = {},
            chatHistoryList = listOf(),
        )
    }
}
