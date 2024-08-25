package com.sjh.autosummary.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.data.repository.HistoryRepository
import com.sjh.autosummary.core.domain.UpdateChatSummaryUseCase
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.MessageContent
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
    private val chatRepository: ChatRepository,
    private val historyRepository: HistoryRepository,
    private val updateChatSummaryUseCase: UpdateChatSummaryUseCase,
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

            is MainScreenEvent.OnSearchClick -> {
                requestChatResponse(event.message)
            }

            MainScreenEvent.OnHistoryClick -> {
                saveChatHistory()
            }
        }
    }

    private fun saveChatHistory(): Job = viewModelScope.launch {
        container.orbit {
            val currentUiState =
                state.chatHistoryState as? LoadState.Succeeded ?: return@orbit

            historyRepository.addOrUpdateChatHistory(
                chatHistory = currentUiState.data
            )
        }
    }

    private fun createChatHistory(): Job = viewModelScope.launch {
        container.orbit {
            if (state.chatHistoryState is LoadState.Succeeded) return@orbit

            val newChatHistory = getInitialChatHistory()

            val chatHistoryId =
                historyRepository.addOrUpdateChatHistory(newChatHistory) ?: return@orbit

            reduce {
                state.copy(
                    chatHistoryState = LoadState.Succeeded(
                        newChatHistory.copy(id = chatHistoryId)
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
                messageList = emptyList(),
            )

            val chatHistoryResult = historyRepository
                .findChatHistory(chatHistoryId)
                .getOrNull()

            if (chatHistoryResult != null) {
                chatHistory = ChatHistory(
                    id = chatHistoryId,
                    date = chatHistoryResult.date,
                    messageList = chatHistoryResult.messageList,
                    name = chatHistoryResult.name
                )
            } else {
                TODO("데이터 불러오기 실패 토스트 띄우기")
            }

            reduce {
                state.copy(
                    chatHistoryState = LoadState.Succeeded(chatHistory)
                )
            }
        }
    }

    private fun requestChatResponse(message: String): Job = viewModelScope.launch {
        container.orbit {
            val currentChatHistoryUiState =
                state.chatHistoryState as? LoadState.Succeeded ?: return@orbit

            if (state.gptResponseState !is LoadState.Succeeded) return@orbit

            val currentChatHistory = currentChatHistoryUiState.data
            val chatMessageList = currentChatHistory.messageList.toMutableList()

            val myMessage = MessageContent(
                content = message,
                role = ChatRoleType.USER
            )

            reduce {
                chatMessageList += myMessage

                state.copy(
                    gptResponseState = LoadState.InProgress,
                    chatHistoryState = LoadState.Succeeded(currentChatHistory.copy(messageList = chatMessageList.toList()))
                )
            }

            val chatResponseResult = chatRepository.requestChatResponse(ChatRequest(myMessage))

            if (chatResponseResult.isFailure) {
                reduce {
                    chatMessageList += getErrorMessageContent(
                        chatResponseResult.exceptionOrNull().toString()
                    )

                    state.copy(
                        gptResponseState = LoadState.Succeeded(false),
                        chatHistoryState = LoadState.Succeeded(
                            currentChatHistory.copy(
                                messageList = chatMessageList.toList()
                            )
                        )
                    )
                }
                return@orbit
            }

            val gptResponseResult = chatResponseResult.getOrNull()

            val gptMessage =
                gptResponseResult?.responseMessage ?: getErrorMessageContent("답변 결과 없음")

            if (gptResponseResult != null) updateChatSummaryUseCase(gptMessage)

            reduce {
                val gptResponseState = gptResponseResult != null
                chatMessageList += gptMessage
                state.copy(
                    gptResponseState = LoadState.Succeeded(gptResponseState),
                    chatHistoryState = LoadState.Succeeded(
                        currentChatHistory.copy(
                            messageList = chatMessageList.toList()
                        )
                    )
                )
            }
        }
    }

    private fun getErrorMessageContent(errorMessage: String) = MessageContent(
        content = errorMessage,
        role = ChatRoleType.GPT
    )

    private fun getInitialChatHistory() = ChatHistory(
        date = formatDate(LocalDate.now()),
        messageList = emptyList()
    )

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }
}
