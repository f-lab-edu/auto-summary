package com.sjh.autosummary.feature.summary.contract.event

import com.sjh.autosummary.core.model.ChatSummary

sealed class SummaryScreenEvent {
    data class onChatSummaryLongClick(val chatSummary: ChatSummary) : SummaryScreenEvent()
    data class onChatSummaryClick(val chatSummary: ChatSummary) : SummaryScreenEvent()
}
