package com.sjh.autosummary.core.data.repository.impl

import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.common.const.GptConst
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.model.ChatResponse
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.MessageContent
import com.sjh.autosummary.core.network.NetworkDataSource
import com.sjh.autosummary.core.network.model.GptChatRequest
import com.sjh.autosummary.core.network.model.GptChatResponse
import com.sjh.autosummary.core.network.model.GptMessageContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
) : ChatRepository {
    override fun createChatCompletion(chatRequest: ChatRequest): Flow<LoadState<ChatResponse>> =
        flow {
            val requestResult =
                networkDataSource.createChatCompletion(
                    chatRequest = chatRequest.toGptChatRequest(),
                )

            if (requestResult.isSuccess) {
                val requestSucceededResult = requestResult.getOrNull()

                if (requestSucceededResult != null) {
                    emit(LoadState.Succeeded(data = requestSucceededResult.toChatResponse()))
                } else {
                    emit(LoadState.Failed(exception = Exception("Received null response from API")))
                }
            } else {
                val requestFailedResult = requestResult.exceptionOrNull()

                if (requestFailedResult != null) {
                    emit(LoadState.Failed(exception = requestFailedResult))
                } else {
                    emit(LoadState.Failed(exception = Exception("Unknown error occurred")))
                }
            }
        }.catch { e ->
            emit(LoadState.Failed(exception = e))
        }.onStart {
            emit(LoadState.InProgress)
        }.flowOn(Dispatchers.IO)
}

private fun GptChatResponse.toChatResponse(): ChatResponse {
    val firstChoice = choices.firstOrNull()
    return ChatResponse(
        responseMessage = firstChoice?.message?.toMessageContent(),
    )
}

private fun ChatRequest.toGptChatRequest() =
    GptChatRequest(
        messages = listOf(
            GptConst.DEFAULT_REQUEST_MESSAGE,
            requestMessage,
        ).map(MessageContent::toGptMessageContent),
        model = GptConst.DEFAULT_GPT_MODEL,
    )

private fun GptMessageContent.toMessageContent(): MessageContent {
    val roleType: ChatRoleType = ChatRoleType.getFromRole(role = this.role) ?: ChatRoleType.SYSTEM
    return MessageContent(
        content = content,
        role = roleType,
    )
}

private fun MessageContent.toGptMessageContent() =
    GptMessageContent(
        content = content,
        role = role.role,
    )
