package com.sjh.autosummary.feature.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.model.ChatResponse
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.data.repository.HistoryRepository
import com.sjh.autosummary.core.data.repository.SummaryRepository
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
    private val summaryRepository: SummaryRepository,
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

            is MainScreenEvent.OnSearchClick -> requestChatResponseAndSummary(event.message)
            MainScreenEvent.OnHistoryClick -> saveChatHistoryAndMoveToHistory()
        }
    }

    /** chat 기록을 저장하고, 화면을 history screen으로 전환을 시도합니다. */
    private fun saveChatHistoryAndMoveToHistory(): Job = viewModelScope.launch {
        container.orbit {
            val currentUiState =
                state.chatHistoryState as? LoadState.Succeeded ?: return@orbit

            historyRepository.addOrUpdateChatHistory(
                chatHistory = currentUiState.data
            )

            postSideEffect(MainScreenSideEffect.MoveToHistoryScreen)
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

    private fun requestChatResponseAndSummary(message: String): Job = viewModelScope.launch {
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

            val chatResponseResult = chatRepository
                .requestChatResponse(myMessage)

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

            if (gptResponseResult != null) updateChatSummary(gptResponseResult)

            val gptMessage =
                gptResponseResult?.responseMessage ?: getErrorMessageContent("답변 결과 없음")

            reduce {
                val gptResponseState = gptResponseResult != null
                chatMessageList += gptMessage
                state.copy(
                    gptResponseState = LoadState.Succeeded(gptResponseState),
                    chatHistoryState = LoadState.Succeeded(
                        currentChatHistory.copy(messageList = chatMessageList.toList())
                    )
                )
            }
        }
    }

    private suspend fun updateChatSummary(chatResponse: ChatResponse): Result<Boolean> {
        try {
            val responseMessage = chatResponse.responseMessage ?: return Result.success(false)

            // 1. 저장된 모든 요약 정보 가져오기 (실패 시 새로운 답변 정보만 저장한다.)
            val retrieveResult = summaryRepository
                .retrieveAllChatSummariesInJson()
                .getOrNull()
                .orEmpty()

            val firstSummary = retrieveResult
                .firstOrNull()
                .orEmpty()

            Log.d("whatisthis", "firstSummary $firstSummary")

            // 2. 답변 요약하기 (실패 시 기존 답변을 그대로 사용한다.)
            val responseSummaryResult = chatRepository
                .requestChatResponseSummary(responseMessage)
                .getOrNull() ?: chatResponse

            val responseSummaryMessage =
                responseSummaryResult.responseMessage ?: return Result.success(false)

            // 3. 요약된 답변 내용과 모든 요약 정보를 합쳐 요청 메시지 생성후 요약 요청 (실패 시 요약된 답변만 저장)
            val responseSummaryUpdateResult = chatRepository
                .requestChatSummaryUpdate(
                    firstSummary,
                    responseSummaryMessage
                )
                .getOrNull() ?: responseSummaryResult

            Log.d("whatisthis", "responseSummaryUpdateResult $responseSummaryUpdateResult")

            val responseSummaryUpdateMessage =
                responseSummaryUpdateResult.responseMessage ?: return Result.success(false)

            // 4. 새로운 요약 정보로 데이터 갱신
            val updateSummaryResult =
                summaryRepository.addOrUpdateChatSummary(responseSummaryUpdateMessage)
            if (updateSummaryResult.isEmpty()) return Result.success(false)

            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
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
