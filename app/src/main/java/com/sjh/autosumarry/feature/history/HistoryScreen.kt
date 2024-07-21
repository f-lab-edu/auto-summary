package com.sjh.autosumarry.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HistoryRoute(
    onSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HistoryScreen(onSummaryClick = onSummaryClick, modifier = modifier)
}

@Composable
fun HistoryScreen(
    onSummaryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
}
