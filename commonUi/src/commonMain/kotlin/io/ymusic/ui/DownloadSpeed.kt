package io.ymusic.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.ymusic.download.DownloadState
import io.ymusic.download.DownloadTask
import kotlin.time.DurationUnit
import kotlin.time.TimeMark
import kotlin.time.TimeSource

private class SpeedState(
    var bytes: Long,
    var mark: TimeMark,
    var smooth: Double,
)

/**
 * Kecepatan download (byte/detik) dari selisih downloadedBytes dan jam internal,
 * dihaluskan supaya tidak loncat-loncat. Hasil seperti "2.4 MB/s", atau "" kalau belum ada data.
 */
@Composable
internal fun rememberDownloadSpeed(
    key: Any,
    downloadedBytes: Long,
): String {
    val s = remember(key) { SpeedState(downloadedBytes, TimeSource.Monotonic.markNow(), 0.0) }
    if (downloadedBytes > s.bytes) {
        val seconds = s.mark.elapsedNow().toDouble(DurationUnit.SECONDS)
        if (seconds >= 0.5) {
            val instant = (downloadedBytes - s.bytes) / seconds
            s.smooth = if (s.smooth == 0.0) instant else s.smooth * 0.6 + instant * 0.4
            s.bytes = downloadedBytes
            s.mark = TimeSource.Monotonic.markNow()
        }
    } else if (downloadedBytes < s.bytes) {
        s.bytes = downloadedBytes
        s.mark = TimeSource.Monotonic.markNow()
        s.smooth = 0.0
    }
    return if (s.smooth > 0.0) formatSpeed(s.smooth) else ""
}

private fun formatSpeed(bytesPerSecond: Double): String {
    val units = listOf("B/s", "KB/s", "MB/s", "GB/s")
    var value = bytesPerSecond
    var index = 0
    while (value >= 1024.0 && index < units.lastIndex) {
        value /= 1024.0
        index++
    }
    val tenths = (value * 10).toLong()
    return "${tenths / 10}.${tenths % 10} ${units[index]}"
}

/** Dipanggil dari DownloadsScreen: "  •  2.4 MB/s" saat DOWNLOADING, kosong selain itu. */
@Composable
internal fun downloadSpeedSuffix(task: DownloadTask): String {
    val speed = rememberDownloadSpeed(task.id, task.downloadedBytes)
    return if (task.state == DownloadState.DOWNLOADING && speed.isNotEmpty()) "  •  $speed" else ""
}
