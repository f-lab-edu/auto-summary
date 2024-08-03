package com.sjh.autosummary.feature.history

import androidx.lifecycle.ViewModel
import com.sjh.autosummary.core.data.repository.HistoryRepository
import javax.inject.Inject

class HistoryViewModel @Inject
constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    init {

    }
}
