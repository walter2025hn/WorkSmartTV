package com.worksmart.iptv.utils

import com.worksmart.iptv.model.Channel
import com.worksmart.iptv.model.ChannelType
import java.net.URL

class M3UParser {
    
    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        
        var currentName = ""
        var currentLogo = ""
        var currentGroup = "General"
        var currentEpgId = ""
        
        for (line in lines) {
            when {
                line.startsWith("#EXTINF:") -> {
                    // Parsear información del canal
                    currentName = extractAttribute(line, "tvg-name") 
                        ?: line.substringAfterLast(",").trim()
                    currentLogo = extractAttribute(line, "tvg-logo") ?: ""
                    currentGroup = extractAttribute(line, "group-title") ?: "General"
                    currentEpgId = extractAttribute(line, "tvg-id") ?: ""
                }
                line.isNotBlank() && !line.startsWith("#") -> {
                    // URL del stream
                    val channel = Channel(
                        id = channels.size.toString(),
                        name = currentName,
                        logo = currentLogo,
                        group = currentGroup,
                        url = line.trim(),
                        epgId = currentEpgId
                    )
                    channels.add(channel)
                    
                    // Reset
                    currentName = ""
                    currentLogo = ""
                    currentGroup = "General"
                    currentEpgId = ""
                }
            }
        }
        
        return channels
    }
    
    private fun extractAttribute(line: String, attribute: String): String? {
        val regex = "$attribute=\"([^\"]*)\"".toRegex()
        return regex.find(line)?.groupValues?.get(1)
    }
}
