package com.sjh.autosummary.feature.main

import androidx.lifecycle.ViewModel
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel()
