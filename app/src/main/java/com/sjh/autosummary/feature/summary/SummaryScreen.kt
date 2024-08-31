package com.sjh.autosummary.feature.summary

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.InformationForm
import com.sjh.autosummary.feature.summary.contract.event.SummaryScreenEvent
import com.sjh.autosummary.feature.summary.contract.sideeffect.SummaryScreenSideEffect
import com.sjh.autosummary.feature.summary.contract.state.SummaryScreenState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SummaryRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SummaryViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.handleEvent(SummaryScreenEvent.ShowAllChatSummary)
    }

    var summaryInformationDetail: ChatSummary? by remember { mutableStateOf(null) }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SummaryScreenSideEffect.ShowToast ->
                Toast
                    .makeText(
                        context,
                        sideEffect.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()

            is SummaryScreenSideEffect.MoveToSummaryScreenDetailScreen -> {
                summaryInformationDetail = sideEffect.chatSummary
            }
        }
    }

    SummaryScreen(
        state = state,
        onBackClick = onBackClick,
        onChatSummaryClick = { chatSummary ->
            viewModel.handleEvent(SummaryScreenEvent.OnChatSummaryClick(chatSummary))
        },
        onChatSummaryLongClick = { chatSummary ->
            viewModel.handleEvent(SummaryScreenEvent.OnChatSummaryLongClick(chatSummary))
        },
        modifier = modifier
    )

    if (summaryInformationDetail != null) {
        SummaryInformationDetail(
            onCloseClick = {
                summaryInformationDetail = null
            },
            summary = summaryInformationDetail!!,
            modifier = modifier,
        )
    }
}

@Composable
fun SummaryScreen(
    state: SummaryScreenState,
    onBackClick: () -> Unit,
    onChatSummaryClick: (ChatSummary) -> Unit,
    onChatSummaryLongClick: (ChatSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SummaryTopBar(onBackClick = onBackClick)
        },
    ) { padding ->
        Box(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            SummaryContent(
                chatSummaryState = state.chatSummaryState,
                onChatSummaryClick = onChatSummaryClick,
                onChatSummaryLongClick = onChatSummaryLongClick,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back",
                    )
                }
                Text("Summary")
            }
        },
        modifier = modifier.statusBarsPadding(),
    )
}

@Composable
fun SummaryContent(
    chatSummaryState: LoadState<List<ChatSummary>>,
    onChatSummaryClick: (ChatSummary) -> Unit,
    onChatSummaryLongClick: (ChatSummary) -> Unit,
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
            when (chatSummaryState) {
                LoadState.InProgress ->
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                is LoadState.Succeeded ->
                    itemsIndexed(chatSummaryState.data) { idx, summary ->
                        SummaryInformation(
                            summary = summary,
                            onChatSummaryClick = {
                                onChatSummaryClick(summary)
                            },
                            onChatSummaryLongClick = {
                                onChatSummaryLongClick(summary)
                            },
                            modifier = modifier,
                        )
                    }

                is LoadState.Failed -> {}
            }
        }
    }
}

@Composable
fun SummaryInformation(
    summary: ChatSummary,
    onChatSummaryClick: () -> Unit,
    onChatSummaryLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onChatSummaryLongClick()
                    },
                    onTap = {
                        onChatSummaryClick()
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
                text = summary.title,
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

@Composable
fun SummaryInformationDetail(
    onCloseClick: () -> Unit,
    summary: ChatSummary,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SummaryDetailTopBar(
                onCloseClick = onCloseClick,
                summaryTitle = summary.title
            )
        },
    ) { padding ->
        Box(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            SummaryContentDetail(
                summary = summary,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailTopBar(
    onCloseClick: () -> Unit,
    summaryTitle: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(text = summaryTitle, fontSize = 32.sp)
        },
        actions = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_x),
                    contentDescription = "Close",
                )
            }
        },
        modifier = modifier.statusBarsPadding(),
    )
}

@Composable
fun SummaryContentDetail(
    summary: ChatSummary,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        if (summary.subTitle != null) {
            item {
                Text(text = summary.subTitle, fontSize = 13.sp)
            }
        }
        item {
            Spacer(modifier = modifier.height(height = 8.dp))
        }
        items(summary.content.size) { idx ->
            BasicInformationForm(
                informationForm = summary.content[idx],
                headFontSize = 24,
                modifier = modifier,
            )
            Spacer(modifier = modifier.height(height = 8.dp))
        }
    }
}

@Composable
fun BasicInformationForm(
    informationForm: InformationForm,
    headFontSize: Int,
    modifier: Modifier = Modifier,
) {
    val adjustedHeadFontSize = if (headFontSize >= 18) headFontSize else 18

    Column(
        modifier = modifier
            .padding(start = 8.dp, top = 4.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(text = informationForm.head, modifier = modifier, fontSize = adjustedHeadFontSize.sp)
        Text(text = informationForm.body, modifier = modifier, fontSize = 14.sp)
        for (information in informationForm.childInformations) {
            BasicInformationForm(
                informationForm = information,
                headFontSize = headFontSize - 2,
                modifier = modifier,
            )
        }
    }
}

@Preview
@Composable
private fun SummaryScreenPreview() {
    AutoSummaryTheme {
        SummaryScreen(
            state = SummaryScreenState(),
            onChatSummaryClick = { a -> },
            onChatSummaryLongClick = { a -> },
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun SummaryContentDetailPreview() {
    AutoSummaryTheme {
        SummaryInformationDetail(
            onCloseClick = {},
            summary = ChatSummary(
                id = 0L,
                title = "제목 01",
                subTitle = "부제목",
                content = listOf(
                    InformationForm(
                        head = "소제목 1",
                        body = "내용 내용 내용 내용",
                        childInformations = listOf(
                            InformationForm(
                                head = "소소제목 1",
                                body = "자세한 내용, 자세한 내용",
                            ),
                            InformationForm(
                                head = "소소제목 2",
                                body = "자세한 내용, 자세한 내용",
                            )
                        ),
                    ),
                    InformationForm(
                        head = "소제목 2",
                        body = "내용 내용 내용 내용",
                        childInformations = listOf(
                            InformationForm(
                                head = "소소제목 1",
                                body = "자세한 내용, 자세한 내용",
                            ),
                            InformationForm(
                                head = "소소제목 2",
                                body = "자세한 내용, 자세한 내용",
                            )
                        ),
                    ),
                ),
            ),
        )
    }
}
