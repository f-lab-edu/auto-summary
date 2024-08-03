package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.model.ChatResponse
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    /** viewmodel에서 LoadState.InProgress에 대한 ui 처리를 하고 싶은데 suspend로 구현하는 방법이 안떠올라 flow로 처리하였습니다! */
    fun createChatCompletion(chatRequest: ChatRequest): Flow<LoadState<ChatResponse>>
}
