package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.data.repository.SummaryRepository
import com.sjh.autosummary.core.database.LocalSummaryDataSource
import com.sjh.autosummary.core.database.room.entity.ChatSummaryEntity
import com.sjh.autosummary.core.model.ChatSummary
import com.sjh.autosummary.core.model.MessageContent
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val localSummaryDataSource: LocalSummaryDataSource,
    private val json: Json,
) : SummaryRepository {

    override suspend fun addOrUpdateChatSummary(chatSummaryContent: MessageContent): List<Long> {
        val extractedJson = extractJsonString(chatSummaryContent.content) ?: return emptyList()
        return try {
            convertJsonToChatSummary(extractedJson)
                .mapNotNull {
                    localSummaryDataSource
                        .insertChatSummary(it.toChatSummaryEntity())
                        .getOrNull()
                }
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            emptyList<Long>()
            TODO("json 형식으로 변환을 실패했을 경우, 답변만 다시 json 형식으로 변경을 시도해 Summary에 추가한다.")
        }
    }

    override suspend fun findChatSummary(chatSummaryId: Long): Result<ChatSummary?> =
        try {
            localSummaryDataSource
                .getChatSummaryById(chatSummaryId)
                .mapCatching { entity ->
                    entity?.toChatSummary()
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
                    entities.map(ChatSummaryEntity::toChatSummary)
                }
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
        }

    override suspend fun deleteChatSummary(chatSummary: ChatSummary): Result<Unit> =
        try {
            localSummaryDataSource
                .deleteChatSummary(chatSummary.toChatSummaryEntity())
        } catch (e: Exception) {
            Log.e("whatisthis", e.toString())
            Result.failure(e)
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

private fun ChatSummary.toChatSummaryEntity() = ChatSummaryEntity(
    id = id,
    title = title,
    subTitle = subTitle,
    content = childInformations
)

private fun ChatSummaryEntity.toChatSummary() = ChatSummary(
    id = id,
    title = title,
    subTitle = subTitle,
    childInformations = content
)
