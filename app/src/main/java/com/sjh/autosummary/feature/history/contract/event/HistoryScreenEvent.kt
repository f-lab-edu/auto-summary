package com.sjh.autosummary.feature.history.contract.event

import com.sjh.autosummary.core.model.ChatHistory

sealed class HistoryScreenEvent {
    data object ShowAllChatHistory : HistoryScreenEvent()
    data class OnChatHistoryLongClick(val chatHistory: ChatHistory) : HistoryScreenEvent()
}
