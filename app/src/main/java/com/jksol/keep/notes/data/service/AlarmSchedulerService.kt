package com.jksol.keep.notes.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.IntentCompat
import com.jksol.keep.notes.MainActivity
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.di.qualifier.ApplicationGlobalScope
import com.jksol.keep.notes.di.qualifier.BulletPointSymbol
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AlarmSchedulerService : Service() {

    @Inject
    lateinit var checklistRepository: Provider<ChecklistRepository>

    @Inject
    lateinit var textNotesRepository: Provider<TextNotesRepository>

    @Inject
    @ApplicationGlobalScope
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    @BulletPointSymbol
    lateinit var bulletPointSymbol: String

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            getString(R.string.notes_notification_channel_id),
            getString(R.string.notes_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.notes_notification_channel_desc)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY
        if (intent.itemId != -1L && intent.itemType != null) {
            when (intent.itemType) {
                TextNote::class.java -> processTextNoteReminder(intent.itemId)
                Checklist::class.java -> processChecklistReminder(intent.itemId)
            }
        }
        if (intent.notificationToRemove != -1) {
            notificationManager.cancel(intent.notificationToRemove)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun processChecklistReminder(checklistId: Long) {
        coroutineScope.launch {
            val checklist = checklistRepository.get().getChecklistById(checklistId) ?: return@launch
            if (checklist.reminderDate == null) {
                return@launch
            }
            val notificationId = buildNotificationIdForItem(checklist)
            showReminderNotification(
                notificationId = notificationId,
                notification = buildChecklistNotification(checklist, notificationId) ?: return@launch
            )
        }
    }

    private fun processTextNoteReminder(noteId: Long) {
        coroutineScope.launch {
            val textNote = textNotesRepository.get().getNoteById(noteId) ?: return@launch
            if (textNote.reminderDate == null) {
                return@launch
            }
            val notificationId = buildNotificationIdForItem(textNote)
            showReminderNotification(
                notificationId = notificationId,
                notification = buildTextNoteNotification(textNote, notificationId) ?: return@launch
            )
        }
    }

    private fun buildTextNoteNotification(textNote: TextNote, notificationId: Int): Notification? {
        return buildNotification(
            title = textNote.title,
            content = textNote.content,
            openItemEditorIntent = getOpenItemEditorPendingIntent(context = this, item = textNote),
            hideNotificationIntent = getHideNotificationPendingIntent(context = this, notificationId = notificationId)
        )
    }

    private fun buildChecklistNotification(checklist: Checklist, notificationId: Int): Notification? {
        val content = buildString {
            checklist.items.forEach { item ->
                if (!item.isChecked) {
                    append(bulletPointSymbol)
                    append(" ")
                    append(item.title)
                    appendLine(" ")
                }
            }
        }
        return buildNotification(
            title = checklist.title,
            content = content,
            openItemEditorIntent = getOpenItemEditorPendingIntent(context = this, item = checklist),
            hideNotificationIntent = getHideNotificationPendingIntent(context = this, notificationId = notificationId),
        )
    }

    private fun buildNotification(
        title: String,
        content: String,
        openItemEditorIntent: PendingIntent,
        hideNotificationIntent: PendingIntent,
    ): Notification? {
        return NotificationCompat.Builder(this, getString(R.string.notes_notification_channel_id))
            .setSmallIcon(R.drawable.ic_circle_notifications)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openItemEditorIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_check, getString(R.string.done), hideNotificationIntent)
            .build()
    }

    private fun showReminderNotification(
        notificationId: Int,
        notification: Notification,
    ) {
        notificationManager.notify(notificationId, notification)
    }

    private fun buildNotificationIdForItem(item: ApplicationMainDataType): Int =
        "${item.id}${item::class.simpleName}".hashCode()

    companion object {

        private const val KEY_ITEM_ID = "item_id"
        private const val KEY_ITEM_TYPE = "item_type"

        private const val KEY_NOTIFICATION_TO_HIDE = "notification_to_hide"

        val Intent.itemId: Long get() = getLongExtra(KEY_ITEM_ID, -1)
        val Intent.itemType: Class<*>? get() = IntentCompat.getSerializableExtra(this, KEY_ITEM_TYPE, Class::class.java)

        val Intent.notificationToRemove: Int get() = getIntExtra(KEY_NOTIFICATION_TO_HIDE, -1)

        fun getIntent(context: Context, target: ApplicationMainDataType): Intent {
            return Intent(context, AlarmSchedulerService::class.java)
                .putExtra(KEY_ITEM_ID, target.id)
                .putExtra(KEY_ITEM_TYPE, target::class.java)
        }

        private fun getHideNotificationPendingIntent(context: Context, notificationId: Int): PendingIntent {
            val intent = Intent(context, AlarmSchedulerService::class.java)
                .putExtra(KEY_NOTIFICATION_TO_HIDE, notificationId)
            return PendingIntent.getService(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun getOpenItemEditorPendingIntent(context: Context, item: ApplicationMainDataType): PendingIntent {
            val intent = MainActivity.getOpenItemEditorIntent(context, item)
            return PendingIntent.getActivity(
                context,
                item.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}