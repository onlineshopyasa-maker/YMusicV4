package io.ymusic.download

import io.ymusic.ytmedia.sabr.AdaptiveFormat

/**
 * Pilihan codec video untuk output MP4. Diisi dari layar pengaturan (UI).
 * Nilai: "auto", "h264", "vp9", atau "av1".
 */
public object Mp4CodecPreference {
    public var value: String = "auto"
}

private fun codecFamily(mimeType: String): String {
    val codecs = mimeType.substringAfter("codecs=", "").trim('"', ' ')
    return when {
        codecs.startsWith("avc1") -> "h264"
        codecs.startsWith("vp9") || codecs.startsWith("vp09") -> "vp9"
        codecs.startsWith("av01") -> "av1"
        else -> "other"
    }
}

/** Urutan untuk mode Otomatis: H.264, lalu VP9, lalu AV1 (angka lebih besar = lebih disukai). */
internal fun mp4CodecRank(mimeType: String): Int =
    when (codecFamily(mimeType)) {
        "h264" -> 3
        "vp9" -> 2
        "av1" -> 1
        else -> 0
    }

/** Untuk output MP4 dengan pilihan codec tertentu, hanya codec itu yang dipertimbangkan (kalau ada). */
internal fun filterMp4Codec(
    video: List<AdaptiveFormat>,
    forMp4: Boolean,
): List<AdaptiveFormat> {
    if (!forMp4) return video
    val wanted = Mp4CodecPreference.value
    if (wanted != "h264" && wanted != "vp9" && wanted != "av1") return video
    val matching = video.filter { codecFamily(it.mimeType) == wanted }
    return matching.ifEmpty { video }
}
