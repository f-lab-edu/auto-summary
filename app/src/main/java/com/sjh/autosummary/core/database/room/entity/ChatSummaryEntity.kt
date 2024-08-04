package com.sjh.autosummary.core.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sjh.autosummary.core.database.room.converter.Converter
import com.sjh.autosummary.core.model.InformationForm

@Entity(tableName = "chat_summary")
data class ChatSummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subTitle: String? = null,
    @TypeConverters(Converter::class) val content: List<InformationForm>
)
