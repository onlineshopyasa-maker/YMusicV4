package io.ymusic.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.ymusic.download.DownloadState
import io.ymusic.download.DownloadTask

/**
 * Menghitung kecepatan download (byte/detik) dari selisih downloadedBytes dan updatedAt,
 * dihaluskan supaya angka tidak loncat-loncat. Mengembalikan teks seperti "2.4 MB/s",
 * atau "" kalau belum ada data.
 */
@Composable
internal fun rememberDownloadSpeed(
    key: Any,
    downloadedBytes: Long,
    updatedAt: Long,
): String {
    // [0] = byte terakhir, [1] = waktu terakhir (ms), [2] = kecepatan halus (byte/detik)
    val state = remember(key) { doubleArrayOf(downloadedBytes.toDouble(), updatedAt.toDouble(), 0.0) }
    val dt = updatedAt.toDouble() - state[1]
    val db = downloadedBytes.toDouble() - state[0]
    if (dt > 0 && db >= 0) {
        val instant = db * 1000.0 / dt
        state[2] = if (state[2] == 0.0) instant else state[2] * 0.7 + instant * 0.3
        state[0] = downloadedBytes.toDouble()
        state[1] = updatedAt.toDouble()
    }
    return if (state[2] > 0.0) formatSpeed(state[2]) else ""
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
    val speed = rememberDownloadSpeed(task.id, task.downloadedBytes, task.updatedAt)
    return if (task.state == DownloadState.DOWNLOADING && speed.isNotEmpty()) "  •  $speed" else ""
}
