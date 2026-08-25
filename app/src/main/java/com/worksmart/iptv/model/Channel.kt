package com.worksmart.iptv.model

data class Channel(
    val id: String,
    val name: String,
    val logo: String?,
    val group: String,
    val url: String,
    val epgId: String? = null,
    val type: ChannelType = ChannelType.LIVE
)

enum class ChannelType {
    LIVE, MOVIE, SERIES
}
