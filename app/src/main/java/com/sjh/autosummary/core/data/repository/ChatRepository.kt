package com.sjh.autosummary.core.data.repository

import com.sjh.autosummary.core.data.model.RequestMessage
import com.sjh.autosummary.core.data.model.ResponseMessage

interface ChatRepository {
    suspend fun receiveChatResponse(requestMessage: RequestMessage): Result<ResponseMessage>
}
