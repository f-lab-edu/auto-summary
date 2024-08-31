package com.sjh.autosummary.feature.main.contract.sideeffect

sealed interface MainScreenSideEffect {
    data class ShowToast(val message: String) : MainScreenSideEffect
    data object MoveToHistoryScreen : MainScreenSideEffect
}
