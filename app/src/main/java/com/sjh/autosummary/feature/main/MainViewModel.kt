package com.sjh.autosummary.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.data.repository.HistoryRepository
import com.sjh.autosummary.core.model.ChatHistory
import com.sjh.autosummary.core.model.MessageContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val chatRepository: ChatRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    private val chatMessages: MutableList<MessageContent> = mutableListOf()
    private var latestChatHistoryId: Long?= null

    init {
        initChat()
    }

    fun initChat() {
        viewModelScope.launch {
            latestChatHistoryId = historyRepository.insertChatHistory(
                ChatHistory(
                    date = getCurrentDate(),
                    messageList = listOf()
                )
            )

            latestChatHistoryId?.let {
                val initialChatHistory = historyRepository.getChatHistory(it)
                /* Todo : State 초기화 */
            }
        }
    }

    fun getChatHistory(chatHistoryId: Long) {
        viewModelScope.launch {
            val pastChatHistory = historyRepository.getChatHistory(chatHistoryId)
            /* Todo : State 초기화 */
        }
    }

    fun requestQuestion(requestMessage: ChatRequest) {
        chatMessages.add(requestMessage.requestMessage)
        viewModelScope.launch {
            chatRepository.createChatCompletion(chatRequest = requestMessage)
                .cancellable()
                .collectLatest { response ->
                    when (response) {
                        LoadState.InProgress -> {
                            TODO("로딩 화면 ")
                        }

                        is LoadState.Succeeded -> {
                            response.data.responseMessage?.let { message ->
                                chatMessages.add(message)
                            }
                            TODO("UI state 갱신 ")
                        }

                        is LoadState.Failed -> {
                            TODO("답변 실패 ")
                        }
                    }
                }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
