package com.sjh.autosummary.feature.history.contract.sideeffect

sealed interface HistoryScreenSideEffect {
    data class Toast(val message: String) : HistoryScreenSideEffect
}
