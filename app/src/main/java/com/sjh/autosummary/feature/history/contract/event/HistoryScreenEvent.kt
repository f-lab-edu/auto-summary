package com.sjh.autosummary.feature.history.contract.event

sealed class HistoryScreenEvent {
    data object OnSummaryClick : HistoryScreenEvent()
}
