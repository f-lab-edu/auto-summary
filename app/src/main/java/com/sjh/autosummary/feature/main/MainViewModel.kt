package com.sjh.autosummary.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.repository.ChatCompletionRepository
import com.sjh.autosummary.core.data.repository.ChatHistoryRepository
import com.sjh.autosummary.core.data.repository.ChatSummaryRepository
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.feature.main.contract.event.MainScreenEvent
import com.sjh.autosummary.feature.main.contract.sideeffect.MainScreenSideEffect
import com.sjh.autosummary.feature.main.contract.state.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@OptIn(OrbitInternal::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val chatCompletionRepository: ChatCompletionRepository,
    private val chatHistoryRepository: ChatHistoryRepository,
    private val chatSummaryRepository: ChatSummaryRepository,
) : ViewModel(),
    ContainerHost<MainScreenState, MainScreenSideEffect> {

    override val container: Container<MainScreenState, MainScreenSideEffect> =
        container(initialState = MainScreenState())

    fun handleEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.StartChat -> {
                if (event.chatHistoryId != 0L) {
                    loadChatHistory(event.chatHistoryId)
                } else {
                    createChatHistory()
                }
            }

            is MainScreenEvent.OnSearchClick -> completeAndSummarizeChat(event.message)
            MainScreenEvent.OnHistoryClick -> saveChatHistoryAndMoveToHistory()
        }
    }

    /** chat 기록을 저장하고, 화면을 history screen으로 전환을 시도합니다. */
    private fun saveChatHistoryAndMoveToHistory(): Job = viewModelScope.launch {
        container.orbit {
            val currentUiState =
                state.chatHistoryState as? LoadState.Succeeded ?: return@orbit

            chatHistoryRepository.addOrUpdateChatHistory(
                chatHistory = currentUiState.data
            )

            postSideEffect(MainScreenSideEffect.MoveToHistoryScreen)
        }
    }

    private fun createChatHistory(): Job = viewModelScope.launch {
        container.orbit {
            if (state.chatHistoryState is LoadState.Succeeded) return@orbit

            val newChatHistory = getInitialChatHistory()

            val addResult =
                chatHistoryRepository.addOrUpdateChatHistory(newChatHistory) ?: return@orbit

            reduce {
                state.copy(
                    chatHistoryState = LoadState.Succeeded(
                        newChatHistory.copy(id = addResult.id)
                    )
                )
            }
        }
    }

    private fun loadChatHistory(chatHistoryId: Long): Job = viewModelScope.launch {
        container.orbit {
            var chatHistory = ChatHistory(
                id = chatHistoryId,
                date = formatDate(LocalDate.now()),
                messages = emptyList(),
            )

            val chatHistoryResult = chatHistoryRepository
                .findChatHistory(chatHistoryId)
                .getOrNull()

            if (chatHistoryResult != null) {
                chatHistory = ChatHistory(
                    id = chatHistoryId,
                    date = chatHistoryResult.date,
                    messages = chatHistoryResult.messages,
                    name = chatHistoryResult.name
                )
            } else {
                postSideEffect(MainScreenSideEffect.ShowToast("데이터 불러오기 실패"))
                postSideEffect(MainScreenSideEffect.MoveToHistoryScreen)
            }

            reduce {
                state.copy(
                    chatHistoryState = LoadState.Succeeded(chatHistory)
                )
            }
        }
    }

    private fun completeAndSummarizeChat(userMessage: String): Job = viewModelScope.launch {
        container.orbit {
            val currentChatHistoryUiState =
                state.chatHistoryState as? LoadState.Succeeded ?: return@orbit
            if (state.responseState !is LoadState.Succeeded) return@orbit

            val currentChatHistory = currentChatHistoryUiState.data

            val updateUserMessageResult = chatHistoryRepository.updateChatHistoryMessages(
                chatHistory = currentChatHistory,
                latestMessage = userMessage,
                isUser = true,
            ) ?: return@orbit

            reduce {
                state.copy(
                    responseState = LoadState.InProgress,
                    chatHistoryState = LoadState.Succeeded(updateUserMessageResult)
                )
            }

            val completeResult = chatCompletionRepository.completeChat(userMessage)

            if (completeResult.isFailure) {
                val updateAssistantFailMessageResult =
                    chatHistoryRepository.updateChatHistoryMessages(
                        chatHistory = (state.chatHistoryState as LoadState.Succeeded).data,
                        latestMessage = completeResult.exceptionOrNull().toString(),
                        isUser = false,
                    ) ?: return@orbit

                reduce {
                    state.copy(
                        responseState = LoadState.Succeeded(false),
                        chatHistoryState = LoadState.Succeeded(
                            currentChatHistory.copy(
                                messages = updateAssistantFailMessageResult.messages.toList()
                            )
                        )
                    )
                }
                return@orbit
            }

            val assistantMessage = completeResult.getOrNull()

            if (assistantMessage != null) chatSummaryRepository.mergeSummaries(assistantMessage)

            val updateAssistantSuccessMessageResult =
                chatHistoryRepository.updateChatHistoryMessages(
                    chatHistory = (state.chatHistoryState as LoadState.Succeeded).data,
                    latestMessage = assistantMessage ?: "답변 결과 없음",
                    isUser = false,
                ) ?: return@orbit

            reduce {
                state.copy(
                    responseState = LoadState.Succeeded(assistantMessage != null),
                    chatHistoryState = LoadState.Succeeded(
                        currentChatHistory.copy(messages = updateAssistantSuccessMessageResult.messages.toList())
                    )
                )
            }
        }
    }

    private fun getInitialChatHistory() = ChatHistory(
        date = formatDate(LocalDate.now()),
        messages = emptyList()
    )

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }
}
