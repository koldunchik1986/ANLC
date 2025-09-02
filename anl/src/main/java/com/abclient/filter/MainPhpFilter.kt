package com.abclient.filter

object MainPhpFilter {
    fun filter(html: String): String {
        var result = html
        // Удаляем DOCTYPE
        result = result.replace(Regex("<!DOCTYPE[^>]*>", RegexOption.IGNORE_CASE), "")
        // Можно добавить другие фильтры по аналогии с ПК-версией
        return result
    }
}
