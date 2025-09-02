package com.abclient.filter

object GameHtmlFilter {
    // Пример фильтрации: замена размеров, добавление JS-функций
    fun filter(html: String): String {
        var result = html
        // Пример: увеличиваем размер чата
        result = result.replace("*,300", "*,400")
        // Пример: добавляем AutoArena JS
        if (result.contains("var ChatClearSize = 12228;")) {
            result = result.replace(
                "var ChatClearSize = 12228;",
                "var ChatClearSize=12228;\n" +
                "var AutoArena = 1;\n" +
                "var AutoArenaTimer = -1;\n" +
                "function arenareload(now) {\n" +
                "  if(!AutoArena && (AutoArenaTimer < 0 || now)) {\n" +
                "    var tm = now ? 1000 : 500;\n" +
                "    AutoArenaTimer = setTimeout('toprefresh('+now+')', tm);\n" +
                "  }\n" +
                "}\n" +
                "function toprefresh(now){\n" +
                "  if(AutoArenaTimer >= 0) {\n" +
                "    clearTimeout(AutoArenaTimer);\n" +
                "    if(!AutoArena) AutoArenaTimer = setTimeout ('toprefresh(0)', 500);\n" +
                "    else AutoArenaTimer = -1;\n" +
                "  }\n" +
                "  if(!AutoArena || now) top.frames['main_top'].location = './main.php';\n" +
                "}\n"
            )
        }
        // Можно добавить другие фильтры по аналогии с ПК-версией
        return result
    }
}
