package com.sjh.autosummary.feature.history

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjh.autosummary.R
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.designsystem.theme.AutoSummaryTheme
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.feature.history.contract.event.HistoryScreenEvent
import com.sjh.autosummary.feature.history.contract.sideeffect.HistoryScreenSideEffect
import com.sjh.autosummary.feature.history.contract.state.HistoryScreenState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HistoryRoute(
    onChatHistoryClick: (Long) -> Unit,
    onSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.handleEvent(HistoryScreenEvent.ShowAllChatHistory)
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HistoryScreenSideEffect.ShowToast ->
                Toast
                    .makeText(context, sideEffect.message, Toast.LENGTH_SHORT)
                    .show()
        }
    }

    HistoryScreen(
        state = state,
        onChatHistoryClick = onChatHistoryClick,
        onChatHistoryLongClick = { chatHistory ->
            viewModel.handleEvent(HistoryScreenEvent.OnChatHistoryLongClick(chatHistory))
        },
        onSummaryClick = onSummaryClick,
        modifier = modifier,
    )
}

@Composable
fun HistoryScreen(
    state: HistoryScreenState,
    onChatHistoryClick: (Long) -> Unit,
    onChatHistoryLongClick: (ChatHistory) -> Unit,
    onSummaryClick: () -> Unit,
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
                chatHistoryState = state.chatHistoryState,
                onChatHistoryClick = onChatHistoryClick,
                onChatHistoryLongClick = onChatHistoryLongClick,
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
    chatHistoryState: LoadState<List<ChatHistory>>,
    onChatHistoryClick: (Long) -> Unit,
    onChatHistoryLongClick: (ChatHistory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        LazyColumn(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            when (chatHistoryState) {
                LoadState.InProgress ->
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                is LoadState.Succeeded ->
                    itemsIndexed(chatHistoryState.data) { idx, history ->
                        ChatHistoryItem(
                            history = history,
                            onChatHistoryClick = {
                                history.id?.let(onChatHistoryClick)
                            },
                            onChatHistoryLongClick = {
                                onChatHistoryLongClick(history)
                            },
                            modifier = modifier,
                        )
                    }

                is LoadState.Failed -> {
                    TODO("로드 실패 토스트 ")
                }
            }
        }
    }
}

@Composable
fun ChatHistoryItem(
    history: ChatHistory,
    onChatHistoryLongClick: () -> Unit,
    onChatHistoryClick: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onChatHistoryLongClick()
                    },
                    onTap = {
                        onChatHistoryClick()
                    }
                )
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
                text = history.date,
                fontSize = 16.sp,
            )

            Text(
                modifier = Modifier.wrapContentSize(),
                text = history.name,
                fontSize = 16.sp,
            )
        }

        Spacer(
            modifier = modifier
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
            onChatHistoryLongClick = { a -> },
            onSummaryClick = {},
            state = HistoryScreenState(),
        )
    }
}
