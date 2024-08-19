package com.sjh.autosummary.feature.history.contract.event

import com.sjh.autosummary.core.model.ChatHistory

sealed class HistoryScreenEvent {
    data class onChatHistoryLongClick(val chatHistory: ChatHistory) : HistoryScreenEvent()
}
