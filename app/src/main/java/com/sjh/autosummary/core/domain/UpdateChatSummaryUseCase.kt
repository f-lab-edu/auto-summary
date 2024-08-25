package com.sjh.autosummary.core.domain

import android.util.Log
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.data.repository.SummaryRepository
import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.MessageContent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UpdateChatSummaryUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(messageContent: MessageContent): Result<Boolean> {
        return try {
            // 1. 저장된 모든 요약 정보 가져오기
            val retrieveResult = summaryRepository.retrieveAllChatSummaries()
            // (요약 불가 처리)
            if (retrieveResult.isFailure) return Result.success(false)

            val firstSummary = retrieveResult
                .getOrThrow()
                .firstOrNull()

            val summariesInJson = convertChatSummaryToJson(firstSummary)
            Log.d("whatisthis", "summariesInJson $summariesInJson")

            // 2. 답변 내용 요약
            val summarizedResponseResult = chatRepository.requestChatResponse(
                ChatRequest(
                    messageContent.copy(
                        content = buildResponseSummarizeRequest(messageContent.content)
                    )
                )
            )

            if (summarizedResponseResult.isFailure) return Result.success(false)

            val summarizedResponse = summarizedResponseResult.getOrThrow().responseMessage

            if (summarizedResponse == null) return Result.success(false)

            // 3. 요약된 답변 내용과 모든 요약 정보를 합쳐 요청 메시지 생성
            val summaryRequestContent =
                buildChatSummarySummarizeRequest(summariesInJson, summarizedResponse.content)
            Log.d("whatisthis", "summaryRequestContent $summaryRequestContent")

            // 4. (모든 요약 + 요약된 답변) 대한 요약 결과
            val summarizedSummaryResult = chatRepository.requestChatResponse(
                ChatRequest(MessageContent(summaryRequestContent, ChatRoleType.USER))
            )
            Log.d("whatisthis", "summarizedSummaryResult $summarizedSummaryResult")

            if (summarizedSummaryResult.isFailure) return Result.success(false)

            // 5. 새로운 요약 정보로 데이터 갱신
            val summarizedSummary = summarizedSummaryResult
                .getOrThrow()
                .responseMessage

            if (summarizedSummary == null) return Result.success(false)

            addOrUpdateChatSummary(summarizedSummary.content)

            Result.success(true)
        } catch (e: Exception) {
            Log.d("whatisthis", e.toString())
            Result.failure(e)
        }
    }

    private fun buildResponseSummarizeRequest(summariesInJson: String): String =
        """
        "${summariesInJson.replace("\"", "\\\"")}"
        위 내용들을 요약해주세요.
        """.trimIndent()

    private fun buildChatSummarySummarizeRequest(
        summariesInJson: String,
        userContent: String
    ): String =
        """
        "$summariesInJson"
        "$userContent"
        위 내용들을 분석하고 요약하여 아래와 같은 JSON 형식으로 구조화해 주세요. 각 주요 항목에 대한 설명과 관련된 세부 정보를 명확히 나누어 작성해 주시기 바랍니다.
        "$chatSummaryInJsonForm"
        """.trimIndent()

    private suspend fun addOrUpdateChatSummary(jsonContent: String) {
        val extractedJson = extractJsonString(jsonContent)
        if (extractedJson != null) {
            val chatSummary = convertJsonToChatSummary(extractedJson)
            summaryRepository.addOrUpdateChatSummary(chatSummary)
        }
    }

    private fun convertChatSummaryToJson(chatSummary: ChatSummary?): String =
        if (chatSummary != null) {
            Json.encodeToString(chatSummary)
        } else {
            ""
        }

    private val chatSummaryInJsonForm = """
{
    "id": 1,
    "title": "",
    "subTitle": "",
    "content": [
        {
            "head": "",
            "body": "",
            "childInformations": [
                {
                    "head": "",
                    "body": "",
                    "childInformations": []
                },
                ...
            ]
        },
        ...
    ]
},
...
    """

    private fun extractJsonString(input: String): String? {
        // JSON 문자열이 시작하는 인덱스 찾기
        val jsonStartIndex = input.indexOf('{')
        // JSON 문자열이 끝나는 인덱스 찾기
        val jsonEndIndex = input.lastIndexOf('}') + 1
        // JSON 문자열이 있는 경우 추출하여 반환
        return if (jsonStartIndex != -1 && jsonEndIndex != -1) {
            input.substring(jsonStartIndex, jsonEndIndex)
        } else {
            // JSON 문자열이 없을 경우 null 반환
            null
        }
    }

    private fun convertJsonToChatSummary(json: String): ChatSummary =
        Json.decodeFromString(json)
}
