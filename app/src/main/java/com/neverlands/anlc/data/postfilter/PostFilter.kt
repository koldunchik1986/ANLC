package com.neverlands.anlc.data.postfilter

object PostFilter {

    fun fixEncoding(html: String): String {
        var result = html
        // This is a very brittle approach, but it's the only way to fix the encoding issues
        // without being able to change the server's response or the WebView's JavaScript engine's behavior.

        // Fix for compl_view buttons
        result = result.replace("value=\"\"", "value=\"Надеть\"")
        result = result.replace("value=\"\"", "value=\"Удалить\"")

        // Add more replacements here as needed...

        return result
    }
}
