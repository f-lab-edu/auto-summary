package com.sjh.autosummary.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.repository.ChatHistoryRepository
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.feature.history.contract.event.HistoryScreenEvent
import com.sjh.autosummary.feature.history.contract.sideeffect.HistoryScreenSideEffect
import com.sjh.autosummary.feature.history.contract.state.HistoryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitInternal::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val chatHistoryRepository: ChatHistoryRepository,
) : ViewModel(),
    ContainerHost<HistoryScreenState, HistoryScreenSideEffect> {

    override val container: Container<HistoryScreenState, HistoryScreenSideEffect> =
        container(initialState = HistoryScreenState())

    fun handleEvent(event: HistoryScreenEvent) {
        when (event) {
            HistoryScreenEvent.ShowAllChatHistory -> fetchAllChatHistroy()
            is HistoryScreenEvent.OnChatHistoryLongClick -> deleteChatHistory(event.chatHistory)
        }
    }

    private fun deleteChatHistory(chatHistory: ChatHistory): Job = viewModelScope.launch {
        container.orbit {
            val currentUiState = state.chatHistoryState as? LoadState.Succeeded ?: return@orbit

            val deleteResult = chatHistoryRepository
                .deleteChatHistory(chatHistory)
                .getOrNull()

            if (deleteResult == null) return@orbit

            val currentChatHistories = currentUiState.data.toMutableList()

            currentChatHistories.remove(chatHistory)

            reduce {
                state.copy(
                    chatHistoryState = LoadState.Succeeded(currentChatHistories.toList())
                )
            }
        }
    }

    private fun fetchAllChatHistroy(): Job = viewModelScope.launch {
        container.orbit {
            if (state.chatHistoryState is LoadState.Succeeded) return@orbit

            val retrieveResult = chatHistoryRepository.retrieveAllChatHistories()

            retrieveResult.fold(
                onSuccess = { foundChatHistories ->
                    reduce {
                        state.copy(chatHistoryState = LoadState.Succeeded(foundChatHistories))
                    }
                },
                onFailure = {
                    postSideEffect(HistoryScreenSideEffect.ShowToast("데이터 불러오기 실패"))
                }
            )
        }
    }
}
