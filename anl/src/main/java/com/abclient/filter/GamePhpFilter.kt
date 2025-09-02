package com.abclient.filter

object GamePhpFilter {
    fun filter(html: String, waitFlash: Boolean = false, userPasswordFlash: String? = null): String {
        var result = html
        // Удаляем DOCTYPE
        result = result.replace(Regex("<!DOCTYPE[^>]*>", RegexOption.IGNORE_CASE), "")
        // Если требуется флеш-пароль
        if (waitFlash && !userPasswordFlash.isNullOrEmpty()) {
            val flashid = "flashvars=\"plid="
            val pos = result.indexOf(flashid, ignoreCase = true)
            if (pos > -1) {
                val pose = result.indexOf('"', pos + flashid.length)
                if (pose > -1) {
                    val pid = result.substring(pos + flashid.length, pose)
                    val sb = StringBuilder()
                    sb.append("<html><head><title>Ввод флеш-пароля...</title></head>")
                    sb.append("<form action=\"./game.php\" method=POST name=ff>")
                    sb.append("<input name=flcheck type=hidden value=\"")
                    sb.append(userPasswordFlash)
                    sb.append("\"> <input name=nid type=hidden value=\"")
                    sb.append(pid)
                    sb.append("\"></form>")
                    sb.append("<script language=\"JavaScript\">document.ff.submit();</script></body></html>")
                    return sb.toString()
                }
            }
        }
        // Можно добавить другие фильтры по аналогии с ПК-версией
        return result
    }
}
