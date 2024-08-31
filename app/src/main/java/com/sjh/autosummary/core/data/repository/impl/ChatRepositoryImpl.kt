package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
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

    override suspend fun requestChatResponse(requestMessage: MessageContent): Result<ChatResponse> =
        requestChatGpt(ChatRequest(requestMessage))

    override suspend fun requestChatResponseSummary(responseContent: MessageContent): Result<ChatResponse> =
        requestChatGpt(
            ChatRequest(
                MessageContent(
                    content = buildSummaryRequest(responseContent.content),
                    role = ChatRoleType.USER
                )
            )
        )

    /** 요약된 답변 내용과 모든 요약 정보를 합쳐 요청 메시지 생성후 요약 요청 */
    override suspend fun requestChatSummaryUpdate(
        summaries: String,
        responseSummaryContent: MessageContent,
    ): Result<ChatResponse> =
        requestChatGpt(
            ChatRequest(
                MessageContent(
                    content = buildChatSummarySummarizeRequest(
                        summaries,
                        responseSummaryContent.content
                    ),
                    role = ChatRoleType.USER
                )
            )
        )

    private suspend fun requestChatGpt(chatRequest: ChatRequest): Result<ChatResponse> =
        withContext(Dispatchers.IO) {
            Log.d("whatisthis", "request : $chatRequest")
            try {
                networkDataSource
                    .createChatCompletion(chatRequest.toGptChatRequest())
                    .mapCatching(GptChatResponse::toChatResponse)
            } catch (e: Exception) {
                Log.e("whatisthis", e.toString())
                Result.failure(e)
            }
        }

    private fun buildSummaryRequest(chatResponse: String): String = """
        "${chatResponse.replace("\"", "\\\"")}"
        위 내용들을 요약해주세요.
        """
        .trimIndent()

    private fun buildChatSummarySummarizeRequest(
        summaries: String,
        responseSummary: String
    ): String = """
        "summaries:$summaries",
        "response:$responseSummary"
        위 내용들을 통합하고 요약해서 JSON 객체를 포함하는 배열로 새롭게 정리해줘(객체가 1개여도 배열로 생성해줘). 각 객체는 다음과 같은 구조를 가져야 해:
        "$chatSummaryInJsonForm"
        (summaries의 객체에 업데이트가 필요한 정보는 summaries와 같은 id로, 새로운 정보는 id를 0으로 해줘)
        """
        .trimIndent()

    private val chatSummaryInJsonForm = """
    {
        "id": 숫자,
        "title": "문자열",
        "subTitle": "문자열",
        "content": [
            {
                "head": "문자열",
                "body": "문자열",
                "childInformations": [
                    {
                        "head": "문자열",
                        "body": "문자열",
                        "childInformations": []
                    },
                    ...
                ]
            },
            ...
        ]
    }
    """
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
