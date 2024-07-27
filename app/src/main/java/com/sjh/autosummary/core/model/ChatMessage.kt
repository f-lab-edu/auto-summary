package com.sjh.autosummary.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(val isFromUser: Boolean, val prompt: String)
