package com.abclient.filter

object JsFilter {
    fun filter(js: String): String {
        var result = js
        // Пример: добавление json2.js
        if (!result.contains("JSON.stringify")) {
            result = "/* json2.js */\n" + result
        }
        // Можно добавить другие фильтры по аналогии с ПК-версией
        return result
    }
}
