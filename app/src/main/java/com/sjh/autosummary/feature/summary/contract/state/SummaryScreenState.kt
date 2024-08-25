package com.sjh.autosummary.feature.summary.contract.state

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.model.ChatSummary

data class SummaryScreenState(
    val chatSummaryState: LoadState<List<ChatSummary>> = LoadState.InProgress,
)
