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
                searchContent(event.content)
            }

            MainScreenEvent.OnHistoryClick -> {
                saveChatHistory()
            }
        }
    }

    private fun saveChatHistory() {
        viewModelScope.launch {
            container.orbit {
                val currentUiState =
                    (state.chatHistoryState as? LoadState.Succeeded) ?: return@orbit

                historyRepository.addOrUpdateChatHistory(
                    chatHistory = currentUiState.data
                )
            }
        }
    }

    private fun createChatHistory() {
        viewModelScope.launch {
            container.orbit {
                if (state.chatHistoryState is LoadState.Succeeded) return@orbit

                val newChatHistory = getInitialChatHistory()

                val chatHistoryId = historyRepository.addOrUpdateChatHistory(
                    chatHistory = newChatHistory
                )

                chatHistoryId?.let { id ->
                    reduce {
                        state.copy(
                            chatHistoryState = LoadState.Succeeded(
                                data = newChatHistory.copy(
                                    id = id
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadChatHistory(chatHistoryId: Long) {
        viewModelScope.launch {
            container.orbit {
                var chatHistory = ChatHistory(
                    id = chatHistoryId,
                    date = getCurrentDate(),
                    messageList = emptyList(),
                )

                val chatHistoryResult = historyRepository.findChatHistory(
                    chatHistoryId = chatHistoryId
                ).getOrNull()

                if (chatHistoryResult != null) {
                    chatHistory = ChatHistory(
                        id = chatHistoryId,
                        date = chatHistoryResult.date,
                        messageList = chatHistoryResult.messageList,
                        name = chatHistoryResult.name
                    )
                } else {
                    /* Todo : 데이터 불러오기 실패 토스트 띄우기 */
                }

                reduce {
                    state.copy(
                        chatHistoryState = LoadState.Succeeded(
                            data = chatHistory
                        )
                    )
                }
            }
        }
    }

    private fun searchContent(content: String) {
        viewModelScope.launch {
            container.orbit {
                val currentChatHistoryUiState =
                    (state.chatHistoryState as? LoadState.Succeeded) ?: return@orbit

                if (state.gptResponseState !is LoadState.Succeeded) return@orbit

                val currentChatHistory = currentChatHistoryUiState.data
                val currentMessageList = currentChatHistory.messageList.toMutableList()

                val myMessage = MessageContent(
                    content = content,
                    role = ChatRoleType.USER
                )
                currentMessageList.add(myMessage)

                reduce {
                    state.copy(
                        gptResponseState = LoadState.InProgress,
                        chatHistoryState = LoadState.Succeeded(
                            data = currentChatHistory.copy(messageList = currentMessageList.toList())
                        )
                    )
                }

                val chatResponseResult = chatRepository.createChatCompletion(
                    chatRequest = ChatRequest(
                        requestMessage = myMessage
                    )
                )
                    .getOrElse { error ->
                        currentMessageList.add(
                            getErrorMessageContent(errorMessage = error.message.toString())
                        )
                        null
                    }


                val gptResponse = if (chatResponseResult != null) {
                    val gptMessage = chatResponseResult.responseMessage

                    if (gptMessage != null) {
                        currentMessageList.add(gptMessage)
                        updateChatSummaryUseCase(gptMessage)
                        true
                    } else {
                        currentMessageList.add(getErrorMessageContent(errorMessage = "답변 결과 없음"))
                        false
                    }
                } else {
                    false
                }

                reduce {
                    state.copy(
                        gptResponseState = LoadState.Succeeded(gptResponse),
                        chatHistoryState = LoadState.Succeeded(
                            data = currentChatHistory.copy(
                                messageList = currentMessageList.toList()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun getErrorMessageContent(errorMessage: String) = MessageContent(
        content = errorMessage,
        role = ChatRoleType.GPT
    )

    private fun getInitialChatHistory() = ChatHistory(
        date = getCurrentDate(),
        messageList = emptyList()
    )

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
}
