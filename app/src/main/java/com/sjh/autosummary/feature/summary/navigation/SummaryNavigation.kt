package com.sjh.autosummary.feature.summary.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sjh.autosummary.feature.summary.SummaryRoute

const val SUMMARY_ROUTE = "/summary"

fun NavController.navigateToSummary() = navigate(SUMMARY_ROUTE)

fun NavGraphBuilder.summaryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = SUMMARY_ROUTE) {
        SummaryRoute(onBackClick)
    }
}
