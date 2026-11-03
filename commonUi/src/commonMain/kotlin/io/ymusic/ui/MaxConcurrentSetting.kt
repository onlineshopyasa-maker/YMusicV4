package io.ymusic.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.ymusic.settings.AppSettings
import io.ymusic.settings.flow
import io.ymusic.settings.put
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** Slider 1..32 di pengaturan unduhan: berapa unduhan yang boleh berjalan bersamaan. */
@Composable
internal fun MaxConcurrentRow(graph: AppGraph) {
    val scope = rememberCoroutineScope()
    val saved by remember(graph) { graph.settings.flow(AppSettings.downloadMaxConcurrent) }.collectAsState()
    var value by remember(saved) { mutableStateOf(saved.toFloat()) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(
                horizontal = DesignSpacing.SettingsRowHorizontal,
                vertical = DesignSpacing.SettingsRowVertical,
            ),
    ) {
        Text(
            "Unduhan bersamaan: ${value.roundToInt()}",
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DesignColors.Ink),
        )
        Slider(
            value = value,
            onValueChange = { value = it },
            onValueChangeFinished = {
                val chosen = value.roundToInt()
                scope.launch { graph.settings.put(AppSettings.downloadMaxConcurrent, chosen) }
            },
            valueRange = 1f..32f,
            steps = 30,
        )
    }
}

/** Dipasang di tempat yang selalu aktif: meneruskan pilihan tersimpan ke antrean unduhan. */
@Composable
internal fun MaxConcurrentSync(graph: AppGraph) {
    val n by remember(graph) { graph.settings.flow(AppSettings.downloadMaxConcurrent) }.collectAsState()
    LaunchedEffect(n, graph.downloads) { graph.downloads?.applyMaxConcurrent(n) }
}
