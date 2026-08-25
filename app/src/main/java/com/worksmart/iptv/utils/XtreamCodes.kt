package com.worksmart.iptv.utils

import com.google.gson.Gson
import com.worksmart.iptv.model.Channel
import com.worksmart.iptv.model.ChannelType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class XtreamCodes {
    
    private val client = OkHttpClient()
    private val gson = Gson()
    
    data class XtreamCredentials(
        val server: String,
        val username: String,
        val password: String
    )
    
    fun authenticate(credentials: XtreamCredentials): Boolean {
        return try {
            val url = "${credentials.server}/player_api.php?" +
                    "username=${credentials.username}&password=${credentials.password}"
            
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    
    fun getChannels(credentials: XtreamCredentials): List<Channel> {
        val channels = mutableListOf<Channel>()
        
        try {
            // Obtener canales en vivo
            val liveUrl = "${credentials.server}/player_api.php?" +
                    "username=${credentials.username}&password=${credentials.password}&action=get_live_streams"
            
            val liveChannels = fetchChannels(liveUrl, ChannelType.LIVE, credentials)
            channels.addAll(liveChannels)
            
            // Obtener películas
            val vodUrl = "${credentials.server}/player_api.php?" +
                    "username=${credentials.username}&password=${credentials.password}&action=get_vod_streams"
            
            val movies = fetchChannels(vodUrl, ChannelType.MOVIE, credentials)
            channels.addAll(movies)
            
            // Obtener series
            val seriesUrl = "${credentials.server}/player_api.php?" +
                    "username=${credentials.username}&password=${credentials.password}&action=get_series"
            
            val series = fetchChannels(seriesUrl, ChannelType.SERIES, credentials)
            channels.addAll(series)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return channels
    }
    
    private fun fetchChannels(apiUrl: String, type: ChannelType, credentials: XtreamCredentials): List<Channel> {
        val list = mutableListOf<Channel>()
        
        val request = Request.Builder().url(apiUrl).build()
        val response = client.newCall(request).execute()
        
        if (response.isSuccessful) {
            val json = response.body?.string() ?: return list
            val jsonArray = org.json.JSONArray(json)
            
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                
                val streamId = obj.optString("stream_id", "")
                val name = obj.optString("name", "Sin nombre")
                val logo = obj.optString("stream_icon", "")
                val category = obj.optString("category_name", "General")
                
                val streamUrl = when (type) {
                    ChannelType.LIVE -> "${credentials.server}/live/${credentials.username}/${credentials.password}/$streamId.m3u8"
                    ChannelType.MOVIE -> "${credentials.server}/movie/${credentials.username}/${credentials.password}/$streamId.${obj.optString("container_extension", "mp4")}"
                    ChannelType.SERIES -> "${credentials.server}/series/${credentials.username}/${credentials.password}/$streamId.${obj.optString("container_extension", "mp4")}"
                }
                
                list.add(Channel(
                    id = streamId,
                    name = name,
                    logo = logo,
                    group = category,
                    url = streamUrl,
                    type = type
                ))
            }
        }
        
        return list
    }
}
