package com.sjh.autosummary.core.data.repository.impl

import android.util.Log
import com.sjh.autosummary.core.data.model.RequestMessage
import com.sjh.autosummary.core.data.model.ResponseMessage
import com.sjh.autosummary.core.data.model.toResponseMessage
import com.sjh.autosummary.core.data.repository.ChatRepository
import com.sjh.autosummary.core.network.NetworkDataSource
import com.sjh.autosummary.core.network.model.GptResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
) : ChatRepository {

    override suspend fun receiveChatResponse(requestMessage: RequestMessage): Result<ResponseMessage> =
        withContext(Dispatchers.IO) {
            Log.d("whatisthis", "receiveChatAnswer requestMessage : $requestMessage")
            try {
                networkDataSource
                    .makeResponse(RequestMessage.makeGptRequest(listOf(requestMessage)))
                    .mapCatching(GptResponse::toResponseMessage)
            } catch (e: Exception) {
                Log.e("whatisthis", e.toString())
                Result.failure(e)
            }
        }
}
