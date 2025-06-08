package com.jksol.keep.notes.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.data.service.AlarmSchedulerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
) {

    fun scheduleAlarm(target: ApplicationMainDataType) {
        val reminderDate = target.reminderDate ?: return
        val triggerTimeMillis = reminderDate.toEpochSecond().times(1000)
        val operation = buildPendingIntent(target)
        alarmManager.setExact(AlarmManager.RTC, triggerTimeMillis, operation)
    }

    fun cancelAlarm(target: ApplicationMainDataType) {
        val operation = buildPendingIntent(target)
        alarmManager.cancel(operation)
    }

    private fun buildPendingIntent(target: ApplicationMainDataType): PendingIntent {
        val intent = AlarmSchedulerService.getIntent(context, target)
        val requestCode = "${target.id}${target::class.simpleName}".hashCode()
        return PendingIntent.getService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}