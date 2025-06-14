package com.jksol.keep.notes.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import com.jksol.keep.notes.core.model.ApplicationMainDataType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val TAG = ReminderSchedulerRepository::class.java.simpleName

class ReminderSchedulerRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
) {

    fun scheduleReminder(target: ApplicationMainDataType) {
        val reminderDate = target.reminderDate ?: return
        val triggerTimeMillis = reminderDate.toEpochSecond().times(1000)
        val operation = buildPendingIntent(target)
        Log.d(TAG, "Scheduling reminder for $target at $reminderDate")
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, operation)
    }

    fun cancelReminder(target: ApplicationMainDataType, removeNotification: Boolean = false) {
        val operation = buildPendingIntent(target)
        Log.d(TAG, "Cancelling reminder for $target")
        if (removeNotification) {
            AlarmSchedulerEventReceiver
                .removeNotificationRequest(context, target)
                .send()
        }
        alarmManager.cancel(operation)
    }

    private fun buildPendingIntent(target: ApplicationMainDataType): PendingIntent {
        val intent = AlarmSchedulerEventReceiver.getIntent(context, target)
        val requestCode = "${target.id}${target::class.simpleName}".hashCode()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}