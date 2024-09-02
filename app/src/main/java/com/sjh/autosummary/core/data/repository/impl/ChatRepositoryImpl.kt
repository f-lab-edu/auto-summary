package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.common.const.GptConst
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.model.ChatResponse
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.MessageContent
import com.sjh.autosummary.core.network.NetworkDataSource
import com.sjh.autosummary.core.network.model.GptChatRequest
import com.sjh.autosummary.core.network.model.GptChatResponse
import com.sjh.autosummary.core.network.model.GptMessageContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val json: Json,
) : ChatRepository {

    override suspend fun requestChatResponse(requestMessage: MessageContent): Result<ChatResponse> =
        requestChatGpt(ChatRequest(requestMessages = listOf(requestMessage)))

    override suspend fun requestChatResponseSummary(responseContent: MessageContent): Result<ChatResponse> =
        requestChatGpt(
            ChatRequest(
                buildSummaryRequest(responseContent.content)
            )
        )

    /** 요약된 답변 내용과 모든 요약 정보를 합쳐 요청 메시지 생성후 요약 요청 */
    override suspend fun requestChatSummaryUpdate(
        chatSummaries: List<ChatSummary>,
        responseSummaryContent: MessageContent,
    ): Result<ChatResponse> = try {
        requestChatGpt(
            ChatRequest(
                buildChatSummarySummarizeRequest(
                    chatSummaries, responseSummaryContent.content
                )
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun requestChatGpt(chatRequest: ChatRequest): Result<ChatResponse> =
        withContext(Dispatchers.IO) {
            Log.d("whatisthis", "request:$chatRequest")
            try {
                networkDataSource.createChatCompletion(chatRequest.toGptChatRequest())
                    .mapCatching(GptChatResponse::toChatResponse)
            } catch (e: Exception) {
                Log.e("whatisthis", e.toString())
                Result.failure(e)
            }
        }

    private fun convertChatSummaryToJson(chatSummary: ChatSummary): String =
        json
            .encodeToString(chatSummary)
            .replace(" ", "")
            .replace("\n", "")

    private fun buildSummaryRequest(chatResponse: String): List<MessageContent> = listOf(
        MessageContent(
            content = """
        "${chatResponse.replace("\"", "\\\"")}"
        위 내용들을 요약해주세요.
        """.trimIndent(),
            role = ChatRoleType.USER
        )
    )

    private fun buildChatSummarySummarizeRequest(
        chatSummaries: List<ChatSummary>, responseSummary: String
    ): List<MessageContent> {
        // 메시지 콘텐츠 생성
        val summaryMessageContents = chatSummaries.map {
            MessageContent(
                content = "summaries:${convertChatSummaryToJson(it)}",
                role = ChatRoleType.USER
            )
        }

        // 최종 요청 메시지 추가
        val responseMessageContent = MessageContent(
            content = """
            response:$responseSummary

            Please consolidate and summarize the above contents(summaries, response) into an array of JSON objects
            1. If the response relates to the summaries object, update it using the same id (do not omit any fields for updated summaries)
            2. If the response contains new content unrelated to the summaries, use id 0
            3. Do not include unchanged summaries in the output; only provide updated summaries with their ids
            Use array format even for one object.
            Each object should have this structure:
            $chatSummaryInJsonForm
        """.trimIndent(),
            role = ChatRoleType.USER
        )

        return summaryMessageContents + responseMessageContent
    }

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

private fun GptChatResponse.toChatResponse(): ChatResponse = ChatResponse(
    responseMessage = choices
        .first()
        .message
        .toMessageContent()
)

private fun ChatRequest.toGptChatRequest() = GptChatRequest(
    messages = (requestMessages + GptConst.DEFAULT_REQUEST_MESSAGE)
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

private fun MessageContent.toGptMessageContent() = GptMessageContent(
    content = content,
    role = role.role,
)
