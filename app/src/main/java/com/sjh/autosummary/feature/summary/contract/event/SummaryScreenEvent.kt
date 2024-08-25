package com.sjh.autosummary.feature.summary.contract.event

import com.sjh.autosummary.core.model.ChatSummary

sealed class SummaryScreenEvent {
    data class OnChatSummaryLongClick(val chatSummary: ChatSummary) : SummaryScreenEvent()
    data class OnChatSummaryClick(val chatSummary: ChatSummary) : SummaryScreenEvent()
    data object ShowAllChatSummary : SummaryScreenEvent()
}
