package com.sjh.autosummary.feature.history.contract.state

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.model.ChatHistory

data class HistoryScreenState(
    val chatHistoryState: LoadState<List<ChatHistory>> = LoadState.InProgress,
)
