package com.sjh.autosummary.feature.main.contract.event

sealed class MainScreenEvent {
    data object OnHistoryClick : MainScreenEvent()
    data class CreateOrLoadChatHistory(val chatHistoryId: Long) : MainScreenEvent()
    data class OnSearchClick(val content: String) : MainScreenEvent()
}
