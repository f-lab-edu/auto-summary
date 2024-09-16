package com.sjh.autosummary.core.database.room.di

import android.content.Context
import androidx.room.Room
import com.sjh.autosummary.core.database.room.dao.ChatHistoryDao
import com.sjh.autosummary.core.database.room.dao.ChatMessageDao
import com.sjh.autosummary.core.database.room.dao.ChatSummaryDao
import com.sjh.autosummary.core.database.room.db.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideLocalDatabase(
        @ApplicationContext context: Context,
    ): RoomDatabase =
        Room.databaseBuilder(
            context,
            RoomDatabase::class.java,
            "autoSummaryDB.db",
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideChatHistoryDao(roomDatabase: RoomDatabase): ChatHistoryDao =
        roomDatabase.chatHistoryDao()

    @Provides
    @Singleton
    fun provideMessageContentDao(roomDatabase: RoomDatabase): ChatMessageDao =
        roomDatabase.chatMessageDao()

    @Provides
    @Singleton
    fun provideChatSummaryDao(roomDatabase: RoomDatabase): ChatSummaryDao =
        roomDatabase.chatSummaryDao()
}
