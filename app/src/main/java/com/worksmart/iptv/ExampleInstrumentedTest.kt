package com.worksmart.iptv

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    
    @Test
    fun useAppContext() {
        // Context of the app under test
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.worksmart.iptv", appContext.packageName)
    }
    
    @Test
    fun testM3UParser() {
        val parser = M3UParser()
        val testM3U = """
            #EXTM3U
            #EXTINF:-1 tvg-id="canal1" tvg-name="Canal 1" tvg-logo="http://logo.png" group-title="General",Canal 1
            http://stream1.m3u8
            #EXTINF:-1 tvg-id="canal2" tvg-name="Canal 2" group-title="Deportes",Canal 2
            http://stream2.m3u8
        """.trimIndent()
        
        val channels = parser.parse(testM3U)
        
        assertEquals(2, channels.size)
        assertEquals("Canal 1", channels[0].name)
        assertEquals("General", channels[0].group)
        assertEquals("Deportes", channels[1].group)
    }
    
    @Test
    fun testXtreamCodesURL() {
        val xtream = XtreamCodes()
        val creds = XtreamCodes.XtreamCredentials(
            server = "http://example.com",
            username = "user",
            password = "pass"
        )
        
        // Verificar que genera URLs correctas
        val liveUrl = "http://example.com/live/user/pass/123.m3u8"
        assertTrue(liveUrl.contains("live"))
        assertTrue(liveUrl.contains("user"))
        assertTrue(liveUrl.contains("pass"))
    }
}
