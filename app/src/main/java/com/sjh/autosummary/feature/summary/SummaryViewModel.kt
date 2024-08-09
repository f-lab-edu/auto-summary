package com.sjh.autosummary.feature.summary

import androidx.lifecycle.ViewModel
import com.sjh.autosummary.core.data.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
) : ViewModel()
