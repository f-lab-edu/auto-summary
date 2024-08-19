package com.sjh.autosummary.feature.summary.contract.sideeffect

import com.sjh.autosummary.core.model.ChatSummary

sealed interface SummaryScreenSideEffect {
    data class Toast(val message: String) : SummaryScreenSideEffect
    data class SummaryScreenDetailScreen(val chatSummary: ChatSummary) : SummaryScreenSideEffect
}
