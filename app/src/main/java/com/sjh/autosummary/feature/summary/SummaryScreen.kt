package com.sjh.autosummary.feature.summary

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SummaryRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SummaryScreen(onBackClick = onBackClick, modifier = modifier)
}

@Composable
fun SummaryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
}
