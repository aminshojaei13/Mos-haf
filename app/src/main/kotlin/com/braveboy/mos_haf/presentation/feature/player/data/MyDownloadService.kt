/*
package com.braveboy.mos_haf.presentation.feature.player.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import java.io.File

@UnstableApi
class MyDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    CHANNEL_ID,
    CHANNEL_NAME_RESOURCE_ID,
    CHANNEL_DESCRIPTION_RESOURCE_ID
) {

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1
        private const val FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 3000L
        private const val CHANNEL_ID = "quran_download_channel"
        private const val CHANNEL_NAME_RESOURCE_ID =
            com.braveboy.mos_haf.R.string.app_name // آیدی استرینگ
        private const val CHANNEL_DESCRIPTION_RESOURCE_ID =
            com.braveboy.mos_haf.R.string.app_name

        private var downloadManager: DownloadManager? = null

        fun getDownloadManager(context: Context): DownloadManager {
            if (downloadManager == null) {
                val cacheDir = File(context.cacheDir, "quran_downloads")
                val cacheEvictor = LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024) // 100MB
                val simpleCache = SimpleCache(cacheDir, cacheEvictor)

                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val cacheDataSourceFactory = CacheDataSource.Factory()
                    .setCache(simpleCache)
                    .setUpstreamDataSourceFactory(dataSourceFactory)

                downloadManager = DownloadManager(
                    context,
                    simpleCache,
                    cacheDataSourceFactory
                )
            }
            return downloadManager!!
        }
    }

    override fun getDownloadManager(): DownloadManager = getDownloadManager(applicationContext)

    override fun getScheduler() = PlatformScheduler(this, 1)
    override fun getForegroundNotification(
        downloads: List<Download?>?,
        notMetRequirements: Int
    ): Notification? {
        TODO("Not yet implemented")
    }

    override fun getForegroundNotification(Downloads: List<Download>): NotificationCompat.Builder {
        // ایجاد Notification برای سرویس در حال اجرا
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Download Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("دانلود آیات قرآن")
            .setContentText("در حال دانلود...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }
}*/
