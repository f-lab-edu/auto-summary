package com.sjh.autosummary.feature.history.contract.sideeffect

sealed interface HistoryScreenSideEffect {
    data class ShowToast(val message: String) : HistoryScreenSideEffect
}
