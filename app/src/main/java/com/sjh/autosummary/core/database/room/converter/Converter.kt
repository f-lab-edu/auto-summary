package com.sjh.autosummary.core.database.room.converter

import androidx.room.TypeConverter
import com.sjh.autosummary.core.model.InformationForm
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class Converter {
    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }

    @TypeConverter
    fun fromInformationFormList(value: List<InformationForm>): String =
        json.encodeToString(ListSerializer(InformationForm.serializer()), value)

    @TypeConverter
    fun toInformationFormList(value: String): List<InformationForm> =
        json.decodeFromString(ListSerializer(InformationForm.serializer()), value)
}
