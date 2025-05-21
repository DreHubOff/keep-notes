package com.jksol.keep.notes.di

import android.content.Context
import androidx.room.Room
import com.jksol.keep.notes.data.database.AppDatabase
import com.jksol.keep.notes.data.database.dao.ChecklistDao
import com.jksol.keep.notes.data.database.dao.ChecklistItemDao
import com.jksol.keep.notes.data.database.dao.TextNoteDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DB_NAME,
        ).build()
    }

    @Provides
    fun provideTextNoteDao(database: AppDatabase): TextNoteDao {
        return database.textNoteDao()
    }

    @Provides
    fun provideChecklistDao(database: AppDatabase): ChecklistDao {
        return database.checklistDao()
    }

    @Provides
    fun provideChecklistItemDao(database: AppDatabase): ChecklistItemDao {
        return database.checklistItemDao()
    }
}