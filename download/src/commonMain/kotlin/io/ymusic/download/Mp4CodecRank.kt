package io.ymusic.download

/**
 * Urutan codec video untuk output MP4 (angka lebih besar = lebih disukai):
 * H.264 (avc1) -> VP9 -> AV1. AV1 paling akhir karena Snapdragon 845 (Note 9)
 * tidak punya dekoder AV1, jadi file AV1 gagal diputar (E60010).
 */
internal fun mp4CodecRank(mimeType: String): Int {
    val codecs = mimeType.substringAfter("codecs=", "").trim('"', ' ')
    return when {
        codecs.startsWith("avc1") -> 3
        codecs.startsWith("vp9") || codecs.startsWith("vp09") -> 2
        codecs.startsWith("av01") -> 1
        else -> 0
    }
}
