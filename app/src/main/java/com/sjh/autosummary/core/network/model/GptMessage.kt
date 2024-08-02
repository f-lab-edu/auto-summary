package com.sjh.autosummary.core.network.model

import com.sjh.autosummary.core.model.ChatRoleType
import com.sjh.autosummary.core.model.MessageContent
import kotlinx.serialization.Serializable

@Serializable
data class GptMessageContent(
    var content: String,
    var role: String,
)

fun MessageContent.toGptRequestMessage() =
    GptMessageContent(
        content = content,
        role = role.role,
    )

fun GptMessageContent.toMessageContent(): MessageContent {
    val roleType: ChatRoleType = ChatRoleType.getFromRole(role = this.role) ?: ChatRoleType.SYSTEM
    return MessageContent(
        content = content,
        role = roleType,
    )
}
