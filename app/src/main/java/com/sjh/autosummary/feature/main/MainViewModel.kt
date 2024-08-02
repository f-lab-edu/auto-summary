package com.sjh.autosummary.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.DataState
import com.sjh.autosummary.core.common.toDataState
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.model.MessageContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val chatRepository: ChatRepository,
    ) : ViewModel() {
        val chatMessages: MutableList<MessageContent> = mutableListOf()

        fun requestQuestion(requestMessage: ChatRequest) {
            chatMessages.add(requestMessage.requestMessage)
            viewModelScope.launch {
                chatRepository.createChatCompletion(chatRequest = requestMessage)
                    .toDataState()
                    .cancellable()
                    .collectLatest { response ->
                        when (response) {
                            DataState.Loading -> {
                                TODO("로딩 화면 ")
                            }

                            is DataState.Success -> {
                                response.data.responseMessage?.let { message ->
                                    chatMessages.add(message)
                                }
                                TODO("UI state 갱신 ")
                            }

                            is DataState.Error -> {
                                TODO("답변 실패 ")
                            }
                        }
                    }
            }
        }
    }
