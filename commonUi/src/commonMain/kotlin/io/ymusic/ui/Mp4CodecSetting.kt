package io.ymusic.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.ymusic.download.Mp4CodecPreference
import io.ymusic.settings.AppSettings
import io.ymusic.settings.flow
import io.ymusic.settings.put
import kotlinx.coroutines.launch

private val codecOrder = listOf("auto", "h264", "vp9", "av1")
private val codecLabels =
    mapOf(
        "auto" to "Otomatis (hindari AV1)",
        "h264" to "H.264 (maks. 1080p)",
        "vp9" to "VP9",
        "av1" to "AV1",
    )

@Composable
internal fun Mp4CodecRow(graph: AppGraph) {
    val scope = rememberCoroutineScope()
    val current by remember(graph) { graph.settings.flow(AppSettings.downloadMp4Codec) }.collectAsState()
    SettingRow(
        title = "Codec video MP4",
        value = codecLabels[current] ?: codecLabels.getValue("auto"),
    ) {
        val next = codecOrder[(codecOrder.indexOf(current) + 1) % codecOrder.size]
        Mp4CodecPreference.value = next
        scope.launch { graph.settings.put(AppSettings.downloadMp4Codec, next) }
    }
}

@Composable
internal fun Mp4CodecSync(graph: AppGraph) {
    val current by remember(graph) { graph.settings.flow(AppSettings.downloadMp4Codec) }.collectAsState()
    SideEffect { Mp4CodecPreference.value = current }
}
