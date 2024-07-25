package com.sjh.autosummary.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sjh.autosummary.R
import com.sjh.autosummary.core.designsystem.theme.AutoSummaryTheme
import com.sjh.autosummary.core.model.ChatMessage


@Composable
fun MainRoute(
    onHistoryClick: () -> Unit,
    messageList: List<ChatMessage>,
    modifier: Modifier = Modifier,
) {
//    val messageList =
//        listOf(
//            ChatMessage(isFromUser = true, prompt = "질문 질문 질문"),
//            ChatMessage(
//                isFromUser = false,
//                prompt = "대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답 대답",
//            ),
//        )
    MainScreen(
        onHistoryClick = onHistoryClick,
        messageList = messageList,
        modifier = modifier,
    )
}

@Composable
fun MainScreen(
    onHistoryClick: () -> Unit,
    messageList: List<ChatMessage>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            MainTopBar(onHistoryClick = onHistoryClick)
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding),
        ) {
            MainContent(
                messageList = messageList,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text("Search")
        },
        modifier = modifier.statusBarsPadding(),
        actions = {
            IconButton(onClick = onHistoryClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "History",
                )
            }
        },
    )
}

@Composable
fun MainContent(messageList: List<ChatMessage>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        var searchWord by remember { mutableStateOf("") }

        LazyColumn(
            modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = true,
        ) {
            itemsIndexed(messageList) { index, message ->
                if (message.isFromUser) {
                    UserMessageBubble(
                        message = message.prompt,
                    )
                } else {
                    AiMessageBubble(message = message.prompt)
                }
            }
        }

        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                modifier = Modifier.weight(1f),
                value = searchWord,
                onValueChange = { newText -> searchWord = newText },
                placeholder = {
                    Text(text = "검색")
                },
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Rounded.Send,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun UserMessageBubble(message: String) {
    Column(
        modifier = Modifier.padding(start = 100.dp, bottom = 16.dp),
    ) {
        Text(
            modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            text = message,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
fun AiMessageBubble(message: String) {
    Column(
        modifier = Modifier.padding(end = 100.dp, bottom = 16.dp),
    ) {
        Text(
            modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Green)
                .padding(16.dp),
            text = message,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    AutoSummaryTheme {
        MainScreen(
            onHistoryClick = {},
            messageList = listOf()
        )
    }
}

@Preview
@Composable
private fun UserChatItemPreview() {
    AutoSummaryTheme {
        UserMessageBubble("user")
    }
}

@Preview
@Composable
private fun UserModelChatItemPreview() {
    AutoSummaryTheme {
        AiMessageBubble("user")
    }
}
