package com.worksmart.iptv.model

data class Playlist(
    val id: String,
    val name: String,
    val type: PlaylistType,
    val url: String? = null,
    val server: String? = null,
    val username: String? = null,
    val password: String? = null,
    val channels: List<Channel> = emptyList()
)

enum class PlaylistType {
    M3U, XTREAM
}
