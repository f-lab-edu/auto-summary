package com.sjh.autosummary.core.network

import com.sjh.autosummary.core.network.model.GptRequest
import com.sjh.autosummary.core.network.model.GptResponse

interface NetworkDataSource {
    suspend fun makeResponse(request: GptRequest): Result<GptResponse>
}
