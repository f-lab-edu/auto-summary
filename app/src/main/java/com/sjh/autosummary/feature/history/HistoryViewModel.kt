package com.sjh.autosummary.feature.history

import androidx.lifecycle.ViewModel
import com.sjh.autosummary.core.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel()
