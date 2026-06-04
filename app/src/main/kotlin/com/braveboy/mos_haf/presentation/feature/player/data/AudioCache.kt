package com.braveboy.mos_haf.presentation.feature.player.data

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object AudioCache {

    private var simpleCache: SimpleCache? = null

    fun getCache(context: Context): SimpleCache {

        if (simpleCache == null) {

            val cacheSize = 200L * 1024L * 1024L // 200 MB

            val evictor = LeastRecentlyUsedCacheEvictor(cacheSize)

            val databaseProvider = StandaloneDatabaseProvider(context)

            simpleCache = SimpleCache(
                File(context.cacheDir, "audio_cache"),
                evictor,
                databaseProvider
            )
        }

        return simpleCache!!
    }

    fun release() {
        simpleCache?.release()
        simpleCache = null
    }
}

@OptIn(UnstableApi::class)
fun provideCacheDataSource(context: Context): DataSource.Factory {

    val cache = AudioCache.getCache(context)

    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)

    return CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
}