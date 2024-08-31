package com.sjh.autosummary.feature.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.repository.SummaryRepository
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.feature.summary.contract.event.SummaryScreenEvent
import com.sjh.autosummary.feature.summary.contract.sideeffect.SummaryScreenSideEffect
import com.sjh.autosummary.feature.summary.contract.state.SummaryScreenState
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
class SummaryViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
) : ViewModel(),
    ContainerHost<SummaryScreenState, SummaryScreenSideEffect> {

    override val container: Container<SummaryScreenState, SummaryScreenSideEffect> =
        container(initialState = SummaryScreenState())

    fun handleEvent(event: SummaryScreenEvent) {
        when (event) {
            is SummaryScreenEvent.OnChatSummaryLongClick -> deleteChatSummary(event.chatSummary)

            is SummaryScreenEvent.OnChatSummaryClick -> moveToChatSummaryDetailScreen(event.chatSummary)

            SummaryScreenEvent.ShowAllChatSummary -> loadChatSummaries()
        }
    }

    private fun loadChatSummaries(): Job = viewModelScope.launch {
        container.orbit {
            val retrieveResult = summaryRepository
                .retrieveAllChatSummaries()
                .getOrNull()

            if (retrieveResult == null) postSideEffect(SummaryScreenSideEffect.ShowToast("데이터 불러오기 실패"))

            reduce {
                state.copy(
                    chatSummaryState = LoadState.Succeeded(
                        retrieveResult ?: emptyList<ChatSummary>()
                    )
                )
            }
        }
    }

    private fun deleteChatSummary(chatSummary: ChatSummary): Job = viewModelScope.launch {
        container.orbit {
            val currentUiState =
                state.chatSummaryState as? LoadState.Succeeded ?: return@orbit

            val deleteResult = summaryRepository
                .deleteChatSummary(chatSummary)
                .getOrNull()

            if (deleteResult == null) postSideEffect(SummaryScreenSideEffect.ShowToast("데이터 삭제 실패"))

            val chatSummaryList = currentUiState.data.toMutableList()

            chatSummaryList.remove(chatSummary)

            reduce {
                state.copy(
                    chatSummaryState = LoadState.Succeeded(chatSummaryList.toList())
                )
            }
        }
    }

    private fun moveToChatSummaryDetailScreen(chatSummary: ChatSummary): Job =
        viewModelScope.launch {
            container.orbit {
                postSideEffect(
                    SummaryScreenSideEffect.MoveToSummaryScreenDetailScreen(chatSummary)
                )
            }
        }
}
