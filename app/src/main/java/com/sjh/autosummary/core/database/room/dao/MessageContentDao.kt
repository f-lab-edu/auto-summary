package com.sjh.autosummary.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sjh.autosummary.core.database.room.entity.MessageContentEntity

@Dao
interface MessageContentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMessageContent(messageContent: MessageContentEntity): Long
}
