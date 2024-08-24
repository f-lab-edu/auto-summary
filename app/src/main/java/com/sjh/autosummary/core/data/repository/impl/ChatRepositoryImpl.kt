package com.sjh.autosummary.core.data.repository.impl

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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
) : ChatRepository {

    override suspend fun requestChatResponse(chatRequest: ChatRequest): Result<ChatResponse> =
        withContext(Dispatchers.IO) {
            try {
                networkDataSource
                    .createChatCompletion(chatRequest.toGptChatRequest())
                    .mapCatching(GptChatResponse::toChatResponse)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

private fun GptChatResponse.toChatResponse(): ChatResponse =
    ChatResponse(
        responseMessage = choices
            .first()
            .message
            .toMessageContent()
    )

private fun ChatRequest.toGptChatRequest() =
    GptChatRequest(
        messages = listOf(
            GptConst.DEFAULT_REQUEST_MESSAGE,
            requestMessage,
        )
            .map(MessageContent::toGptMessageContent),
        model = GptConst.DEFAULT_GPT_MODEL,
    )

private fun GptMessageContent.toMessageContent(): MessageContent {
    val roleType: ChatRoleType = ChatRoleType.getFromRole(this.role) ?: ChatRoleType.SYSTEM
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
