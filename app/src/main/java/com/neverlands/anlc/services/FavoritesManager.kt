package com.neverlands.anlc.services

import android.content.Context
import com.neverlands.anlc.model.Bookmark
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

object FavoritesManager {

    fun loadFavorites(context: Context): List<Bookmark> {
        val bookmarks = mutableListOf<Bookmark>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            val inputStream: InputStream = context.assets.open("abfavorites.xml")
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "favorite") {
                    val title = parser.getAttributeValue(null, "title") ?: "Без названия"
                    val url = parser.getAttributeValue(null, "url") ?: "http://www.neverlands.ru"
                    val icon = parser.getAttributeValue(null, "icon")
                    bookmarks.add(Bookmark(title, url, icon))
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bookmarks
    }
}
