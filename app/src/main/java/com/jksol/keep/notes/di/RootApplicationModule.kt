package com.jksol.keep.notes.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.jksol.keep.notes.R
import com.jksol.keep.notes.RootApplication
import com.jksol.keep.notes.di.qualifier.ApplicationGlobalScope
import com.jksol.keep.notes.di.qualifier.BulletPointSymbol
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

    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService<AlarmManager>()!!

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService<NotificationManager>()!!

    @Provides
    @BulletPointSymbol
    fun provideBulletPointSymbol(@ApplicationContext context: Context): String =
        context.getString(R.string.bullet_point_symbol)
}