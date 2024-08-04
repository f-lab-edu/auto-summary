package com.sjh.autosummary.core.model

enum class ChatRoleType(val role: String) {
    SYSTEM(role = "system"),
    USER(role = "user"),
    GPT(role = "assistant"),
    ;

    companion object {
        private val roleTypeList: List<ChatRoleType> = entries

        fun getFromRole(role: String): ChatRoleType? = roleTypeList.find { it.role == role }
    }
}
