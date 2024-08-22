package com.sjh.autosummary.feature.summary.contract.sideeffect

import com.sjh.autosummary.core.model.ChatSummary

sealed interface SummaryScreenSideEffect {
    data class ShowToast(val message: String) : SummaryScreenSideEffect
    data class MoveToSummaryScreenDetailScreen(val chatSummary: ChatSummary) : SummaryScreenSideEffect
}
