package com.jksol.keep.notes.di

import android.content.Context
import com.jksol.keep.notes.RootApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object RootApplicationModule {

    @Provides
    @ApplicationGlobalScope
    fun provideApplicationScope(@ApplicationContext context: Context): CoroutineScope {
        return (context as RootApplication).applicationScope
    }
}