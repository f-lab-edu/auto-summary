package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.common.const.GptConst
import com.sjh.autosummary.core.data.repository.ChatSummaryRepository
import com.sjh.autosummary.core.database.LocalSummaryDataSource
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.network.model.GptChatCompletionsRequest
import com.sjh.autosummary.core.network.retrofit.RetrofitGptApiHolder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatSummaryRepositoryImpl @Inject constructor(
    private val localSummaryDataSource: LocalSummaryDataSource,
    private val retrofitGptApiHolder: RetrofitGptApiHolder,
    private val json: Json,
) : ChatSummaryRepository {

    override suspend fun findChatSummary(chatSummaryId: Long): Result<ChatSummary?> =
        try {
            localSummaryDataSource
                .getChatSummaryById(chatSummaryId)
                .mapCatching { entity ->
                    if (entity != null) {
                        ChatSummary(
                            entity.id,
                            entity.title,
                            entity.subTitle,
                            entity.content
                        )
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }

    override suspend fun retrieveAllChatSummaries(): Result<List<ChatSummary>> =
        try {
            localSummaryDataSource
                .getAllChatSummaries()
                .mapCatching { entities ->
                    entities.map { chatSummaryEntity ->
                        ChatSummary(
                            chatSummaryEntity.id,
                            chatSummaryEntity.title,
                            chatSummaryEntity.subTitle,
                            chatSummaryEntity.content
                        )
                    }
                }
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }

    override suspend fun deleteChatSummary(chatSummary: ChatSummary): Result<Unit> =
        try {
            localSummaryDataSource
                .deleteChatSummary(
                    ChatSummaryEntity(
                        chatSummary.id,
                        chatSummary.title,
                        chatSummary.subTitle,
                        chatSummary.childInformations
                    )
                )
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }

    override suspend fun mergeSummaries(content: String): Result<Boolean> {
        try {
            // 1. 답변 요약하기 (실패 시 기존 답변을 그대로 사용한다.)
            val summaryResult = summarize(content).getOrNull() ?: content

            Log.d("whatisthis", "1. summaryResult: $summaryResult")

            // 2. 요약된 답변 내용과 모든 요약 정보를 합쳐 요청 메시지 생성후 요약 요청 (실패 시 요약된 답변만 저장)
            val integrateResult =
                integrateSummaries(summaryResult).getOrNull() ?: summaryResult

            Log.d("whatisthis", "2. integrateResult: $integrateResult")

            // 3. 새로운 요약 정보로 데이터 갱신
            val updateResult = updateSummaries(integrateResult)
            if (updateResult.isEmpty()) return Result.success(false)

            Log.d("whatisthis", "3. updateResult: $updateResult")
            return Result.success(true)
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            return Result.failure(e)
        }
    }

    private suspend fun summarize(originalContent: String): Result<String> =
        completeRequest(
            buildSummaryRequest(originalContent)
        )

    /** 요약된 답변 내용과 모든 요약 정보를 합쳐 새로운 요약문 요청 */
    private suspend fun integrateSummaries(newSummary: String): Result<String> =
        try {
            val summaries = retrieveAllChatSummaries()
                .getOrNull()
                .orEmpty()

            completeRequest(
                buildChatSummarySummarizeRequest(
                    summaries,
                    newSummary
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    private suspend fun completeRequest(message: List<String>): Result<String> =
        try {
            Log.d("whatisthis", "completeRequest message : $message")
            val chatCompletionResponse = retrofitGptApiHolder
                .retrofitGptApi
                .createChatCompletion(
                    GptChatCompletionsRequest(
                        GptConst.DEFAULT_GPT_MODEL,
                        message.map {
                            GptChatCompletionsRequest.Message(
                                content = it,
                                role = "user"
                            )
                        }
                    )
                )

            val assistantMessage = chatCompletionResponse
                .choices
                .first()
                .message
                .content

            Result.success(assistantMessage)
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }

    /** 입력된 chatSummary의 띄어쓰기를 제외하고 모든 공백을 제거해서 반환해주는 메서드 (글자 수를 줄이기 위해) */
    private fun convertChatSummaryToJson(chatSummary: ChatSummary): String =
        json
            .encodeToString(chatSummary)
            .replace(Regex("[\\t\\n\\r]+"), "") // 탭, 줄바꿈, 캐리지 리턴 제거
            .replace(Regex("\\s{2,}"), " ") // 연속된 공백을 하나의 띄어쓰기로 대체
            .trim() // 문자열 양 끝의 공백 제거

    private fun buildSummaryRequest(chatResponse: String): List<String> = listOf(
        """
            "${chatResponse.replace("\"", "\\\"")}"
            위 내용들을 요약해주세요.
        """.trimIndent()
    )

    private fun buildChatSummarySummarizeRequest(
        chatSummaries: List<ChatSummary>,
        responseSummary: String
    ): List<String> {
        val promptizedSummaries = chatSummaries.mapTo(mutableListOf()) {
            "summaries:${convertChatSummaryToJson(it)}"
        }
        return promptizedSummaries + """
        new summary :$responseSummary
    
        Please analyze the 'summaries' array and the 'new summary', and process them as follows:
        1. If the 'new summary' is related to any items in the 'summaries' array:
         - Maintain the ID of each 'summaries' item and appropriately integrate the contents of 'summaries' and the 'new summary'.
         - When integrating, group the items based on their broader topic or category to create a new 'summaries' array.
        2. If the 'new summary' is not related to any items in the 'summaries' array:
         - Create a 'new summary' with an ID set to 0.
        3. Do not include unchanged 'summaries' in the result; only provide updated 'summaries' with their ids.
        4. The result should be in the format of an array of JSON objects. (Even if there is only one object, please use array format.)
        5. Each object must include all fields and should have the following structure, ensuring it accurately reflects the content at each level. The 'childInformations' field is recursive and should be structured accordingly:                $chatSummaryInJsonForm
        """.trimIndent()
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

    private suspend fun updateSummaries(newSummary: String): List<Long> =
        try {
            val extractedJson = extractJsonString(newSummary) ?: throw Exception("Json 형식으로 반환 실패")
            convertJsonToChatSummary(extractedJson)
                .mapNotNull { chatSummary ->
                    localSummaryDataSource
                        .insertChatSummary(
                            ChatSummaryEntity(
                                chatSummary.id,
                                chatSummary.title,
                                chatSummary.subTitle,
                                chatSummary.childInformations
                            )
                        )
                        .getOrNull()
                }
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            emptyList<Long>()
            TODO("json 형식으로 변환을 실패했을 경우, 답변만 다시 json 형식으로 변경을 시도해 Summary에 추가한다.")
        }

    private fun extractJsonString(input: String): String? {
        // JSON 문자열이 시작하는 인덱스 찾기
        val jsonStartIndex = input.indexOf('[')
        // JSON 문자열이 끝나는 인덱스 찾기
        val jsonEndIndex = input.lastIndexOf(']') + 1
        // JSON 문자열이 있는 경우 추출하여 반환
        return if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonStartIndex < jsonEndIndex) {
            input.substring(jsonStartIndex, jsonEndIndex)
        } else {
            // JSON 문자열이 없을 경우 null 반환
            null
        }
    }

    private fun convertJsonToChatSummary(chatSummaryInJson: String): List<ChatSummary> =
        json.decodeFromString(chatSummaryInJson)
}
