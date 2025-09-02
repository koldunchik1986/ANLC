package com.abclient.filter

object MsgPhpFilter {
    fun filter(html: String, chatKeepGame: Boolean = false, chat: String? = null): String {
        var result = html
        if (chatKeepGame && !chat.isNullOrEmpty()) {
            result = result.replace(" id=msg>", " id=msg>" + chat)
        }
        // Можно добавить другие фильтры по аналогии с ПК-версией
        return result
    }
}
