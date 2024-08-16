package com.sjh.autosummary.feature.main.contract.sideeffect

sealed interface MainScreenSideEffect {
    data class Toast(val message: String) : MainScreenSideEffect
}
