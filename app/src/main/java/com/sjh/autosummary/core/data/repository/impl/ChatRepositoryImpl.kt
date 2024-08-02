package com.sjh.autosummary.core.data.repository.impl

import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.model.ChatResponse
import com.sjh.autosummary.core.data.model.toChatResponse
import com.sjh.autosummary.core.data.model.toGptChatRequest
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.network.NetworkDataSource
import com.sjh.autosummary.core.network.model.GptChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl
    @Inject
    constructor(
        private val networkDataSource: NetworkDataSource,
    ) : ChatRepository {
        override fun createChatCompletion(chatRequest: ChatRequest): Flow<ChatResponse> =
            networkDataSource.createChatCompletion(
                chatRequest = chatRequest.toGptChatRequest(),
            )
                .map(GptChatResponse::toChatResponse)
                .catch { e -> throw e }
                .flowOn(Dispatchers.IO)
    }
