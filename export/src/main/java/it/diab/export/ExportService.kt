/*
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.txt
 */
package it.diab.export

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import it.diab.core.data.repositories.GlucoseRepository
import it.diab.core.data.repositories.InsulinRepository
import it.diab.core.util.PreferencesUtil
import it.diab.export.writer.CsvWriter
import it.diab.export.writer.XlsxWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ExportService : Service() {
    private lateinit var glucoseRepository: GlucoseRepository
    private lateinit var insulinRepository: InsulinRepository
    private lateinit var notificationManager: NotificationManager

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()

        glucoseRepository = GlucoseRepository.getInstance(this)
        insulinRepository = InsulinRepository.getInstance(this)
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!job.isCompleted) {
            job.cancel()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelIfNeeded()
        }

        startForeground(RUNNING_NOTIFICATION_ID, notification)

        val targetAction = intent?.getIntExtra(EXPORT_TARGET, -1) ?: -1
        when (targetAction) {
            TARGET_CSV -> exportCsv(this::onTaskCompleted)
            TARGET_XLSX -> exportXlxs(this::onTaskCompleted)
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?) = null

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL)
        .setSmallIcon(R.drawable.ic_export)
        .setContentTitle(getString(R.string.export_notification_title))
        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
        .setProgress(100, 10, true)
        .build()

    private fun buildCompletedNotification(file: File?, success: Boolean) {
        val message = getString(if (success) R.string.export_completed_success else R.string.export_completed_failure)

        val notification = NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_export)
            .setContentTitle(getString(R.string.export_notification_title))
            .setContentText(message)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))

        if (success && file != null && file.exists()) {
            val uri = FileProvider.getUriForFile(this, "it.diab.files", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "application/octet-stream"
            }

            val sharePendingIntent = PendingIntent.getActivity(this, 0,
                Intent.createChooser(shareIntent, getString(R.string.export_share)), PendingIntent.FLAG_CANCEL_CURRENT)

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, contentResolver.getType(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val openPendingIntent = PendingIntent.getActivity(this, 0,
                Intent.createChooser(openIntent, getString(R.string.export_share)), PendingIntent.FLAG_CANCEL_CURRENT)

            notification.addAction(R.drawable.ic_export, getString(R.string.export_share), sharePendingIntent)
                .setContentIntent(openPendingIntent)
                .setAutoCancel(true)
        }

        notificationManager.notify(COMPLETED_NOTIFICATION_ID, notification.build())
    }

    private fun exportCsv(onTaskCompleted: (File?, Boolean) -> Unit) {
        val lowThreshold = PreferencesUtil.getGlucoseLowThreshold(this)
        val highThreshold = PreferencesUtil.getGlucoseHighThreshold(this)

        serviceScope.launch {
            val trainResult = CsvWriter.exportTrain(this, glucoseRepository)

            val testResult = CsvWriter.exportTest(this,
                glucoseRepository,
                lowThreshold..highThreshold
            )

            GlobalScope.launch(Dispatchers.Main) { onTaskCompleted(null, trainResult && testResult) }
        }
    }

    private fun exportXlxs(onTaskCompleted: (File?, Boolean) -> Unit) {
        val glucoseHeaders = listOf(
            getString(R.string.export_sheet_glucose_value),
            getString(R.string.export_sheet_glucose_date),
            getString(R.string.export_sheet_glucose_eat),
            getString(R.string.export_sheet_glucose_insulin),
            getString(R.string.export_sheet_glucose_insulin_value),
            getString(R.string.export_sheet_glucose_basal),
            getString(R.string.export_sheet_glucose_insulin_value)
        )
        val insulinHeaders = listOf(
            getString(R.string.export_sheet_insulin_name),
            getString(R.string.export_sheet_insulin_time_frame),
            getString(R.string.export_sheet_insulin_basal),
            getString(R.string.export_sheet_insulin_half_units)
        )

        serviceScope.launch {
            val result = XlsxWriter.exportSheet(
                this,
                glucoseRepository,
                insulinRepository,
                glucoseHeaders,
                insulinHeaders
            )
            GlobalScope.launch(Dispatchers.Main) { onTaskCompleted(result, result != null) }
        }
    }

    private fun onTaskCompleted(output: File?, result: Boolean) {
        notificationManager.cancel(RUNNING_NOTIFICATION_ID)
        buildCompletedNotification(output, result)
        stopSelf()
    }

    @RequiresApi(26)
    private fun createChannelIfNeeded() {
        val channel = notificationManager.getNotificationChannel(CHANNEL)
        if (channel != null) {
            return
        }

        val name = getString(R.string.export_notification_channel)
        val newChannel = NotificationChannel(CHANNEL, name, NotificationManager.IMPORTANCE_LOW).apply {
            enableLights(false)
            enableVibration(false)
            setShowBadge(false)
            lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
        }

        notificationManager.createNotificationChannel(newChannel)
    }

    companion object {
        const val RUNNING_NOTIFICATION_ID = 1927
        private const val COMPLETED_NOTIFICATION_ID = 1928

        const val EXPORT_TARGET = "export_target"
        const val TARGET_CSV = 0
        const val TARGET_XLSX = 1

        private const val CHANNEL = "exportChannel"
    }
}
