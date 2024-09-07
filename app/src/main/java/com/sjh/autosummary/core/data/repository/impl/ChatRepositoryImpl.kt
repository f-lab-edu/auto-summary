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

    override suspend fun receiveAIAnswer(askMessage: MessageContent): Result<ChatResponse> =
        requestChatGpt(ChatRequest(requestMessages = listOf(askMessage)))

    override suspend fun receiveAISummary(originalMessage: MessageContent): Result<ChatResponse> =
        requestChatGpt(
            ChatRequest(
                buildSummaryRequest(originalMessage.content)
            )
        )

    /** 요약된 답변 내용과 모든 요약 정보를 합쳐 새로운 요약문 요청 */
    override suspend fun receiveAIMergedSummary(
        storedSummaries: List<ChatSummary>,
        newSummary: MessageContent,
    ): Result<ChatResponse> = try {
        requestChatGpt(
            ChatRequest(
                buildChatSummarySummarizeRequest(
                    storedSummaries,
                    newSummary.content
                )
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun requestChatGpt(chatRequest: ChatRequest): Result<ChatResponse> =
        withContext(Dispatchers.IO) {
            Log.d("whatisthis", "requestChatGpt : $chatRequest")
            try {
                networkDataSource.createChatCompletion(chatRequest.toGptChatRequest())
                    .mapCatching(GptChatResponse::toChatResponse)
            } catch (e: Exception) {
                Log.e("whatisthis", e.toString())
                Result.failure(e)
            }
        }

    /** 입력된 chatSummary의 띄어쓰기를 제외하고 모든 공백을 제거해서 반환해주는 메서드 (글자 수를 줄이기 위해) */
    private fun convertChatSummaryToJson(chatSummary: ChatSummary): String =
        json
            .encodeToString(chatSummary)
            .replace(Regex("[\\t\\n\\r]+"), "") // 탭, 줄바꿈, 캐리지 리턴 제거
            .replace(Regex("\\s{2,}"), " ") // 연속된 공백을 하나의 띄어쓰기로 대체
            .trim() // 문자열 양 끝의 공백 제거

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
        chatSummaries: List<ChatSummary>,
        responseSummary: String
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
                new summary :$responseSummary
    
                Please analyze the 'summaries' array and the 'new summary', and process them as follows:
                1. If the 'new summary' is related to any items in the 'summaries' array:
                 - Maintain the ID of each 'summaries' item and appropriately integrate the contents of 'summaries' and the 'new summary'.
                 - When integrating, group the items based on their broader topic or category to create a new 'summaries' array."
                2. If the 'new summary' is not related to any items in the 'summaries' array:
                 - Create a 'new summary' with an ID set to 0.
                3. Do not include unchanged 'summaries' in the result; only provide updated 'summaries' with their ids.
                4. The result should be in the format of an array of JSON objects. (Even if there is only one object, please use array format.)
                5. Each object must include all fields and should have the following structure, ensuring it accurately reflects the content at each level. The 'childInformations' field is recursive and should be structured accordingly:                $chatSummaryInJsonForm
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
    "childInformations": [ // 이 부분이 재귀적 구조입니다.
        {
            "head": "문자열", // 첫 번째 레벨의 childInformations
            "body": "문자열",
            "childInformations": [
                {
                    "head": "문자열", // 두 번째 레벨의 childInformations
                    "body": "문자열",
                    "childInformations": [
                        // 재귀
                    ]
                },
                // 추가 항목
            ]
        },
        // 추가 항목
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
