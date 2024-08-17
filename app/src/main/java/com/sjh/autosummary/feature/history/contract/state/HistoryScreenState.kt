package com.sjh.autosummary.feature.main.contract.state

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.core.model.MessageContent

data class MainScreenState(
    val chatHistoryState: LoadState<ChatHistory> = LoadState.InProgress,
)
