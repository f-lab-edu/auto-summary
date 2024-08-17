package com.sjh.autosummary.core.domain

import android.util.Log
import com.sjh.autosummary.core.data.model.ChatRequest
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.data.repository.SummaryRepository
import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.MessageContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UpdateChatSummaryUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(messageContent: MessageContent): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                // 1. 저장된 모든 요약 정보 가져오기
                val retrieveResult = summaryRepository.retrieveAllChatSummaries()
                // (요약 불가 처리)
                if (retrieveResult.isFailure) return@withContext Result.success(false)

                val summaries = retrieveResult.getOrThrow()
                val summariesInJson = convertChatSummaryToJson(summaries.firstOrNull())
                Log.d("whatisthis", "summariesInJson ${summariesInJson}")

                // 2. 답변 내용 요약
                val summarizedResponseResult = chatRepository.createChatCompletion(
                    chatRequest = ChatRequest(
                        requestMessage = messageContent
                    )
                )
                if (summarizedResponseResult.isFailure) return@withContext Result.success(false)
                summarizedResponseResult.getOrThrow().responseMessage?.let { content ->
                    // 3. 요약된 답변 내용과 모든 요약 정보를 합쳐 요청 메시지 생성
                    val summaryRequestContent =
                        buildRequestFormForSummary(summariesInJson, content.content)
                    Log.d("whatisthis", "summaryRequestContent ${summaryRequestContent}")
                    // 4. 요청 메시지로 요약 요청
                    val requestResult = chatRepository.createChatCompletion(
                        chatRequest = ChatRequest(
                            requestMessage = MessageContent(
                                content = summaryRequestContent,
                                role = ChatRoleType.USER
                            )
                        )
                    )
                    Log.d("whatisthis", "requestResult ${requestResult}")
                    // (요약 실패 처리)
                    if (requestResult.isFailure) return@withContext Result.success(false)
                    // 5. 새로운 요약 정보로 데이터 갱신
                    requestResult.getOrThrow().responseMessage?.let { content ->
                        addOrUpdateChatSummary(content.content)
                    }
                }
                Result.success(true)
            } catch (e: Exception) {
                Log.d("whatisthis", e.toString())
                Result.failure(e)
            }
        }

    private fun buildRequestFormForSummary(summariesInJson: String, userContent: String): String =
        """
        "${summariesInJson.replace("\"", "\\\"")}"
        "${userContent.replace("\"", "\\\"")}"
        위 내용들을 요약해서
        ${chatSummaryInJsonForm}
        이러한 형태의 json 문자열로 작성해줘
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
              "informationForm": {
                "head": "(Nested)",
                "body": "(Nested)",
                "informationForm": {
                  "head": "(Deeply Nested)",
                  "body": "(Deeply Nested)",
                  "informationForm": null
                }
              }
            }
          ]
        }
    """.trimIndent()

    private fun extractJsonString(input: String): String? {
        // JSON 문자열이 시작하는 인덱스 찾기
        val jsonStartIndex = input.indexOf('{')
        // JSON 문자열이 끝나는 인덱스 찾기
        val jsonEndIndex = input.lastIndexOf('}') + 1
        // JSON 문자열이 있는 경우 추출하여 반환
        return if (jsonStartIndex != -1 && jsonEndIndex != -1) {
            input.substring(jsonStartIndex, jsonEndIndex)
        } else {
            null // JSON 문자열이 없을 경우 null 반환
        }
    }

    private fun convertJsonToChatSummary(json: String): ChatSummary =
        Json.decodeFromString(json)
}
