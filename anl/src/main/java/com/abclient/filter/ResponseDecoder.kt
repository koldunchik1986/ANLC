package com.abclient.filter

import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

object ResponseDecoder {
    // Распаковка gzip/deflate, удаление chunked, декодирование windows-1251 (строго как в ABClient)
    fun decode(bytes: ByteArray, contentEncoding: String?, transferEncoding: String?, contentType: String? = null): String {
        var data = bytes
        // Распаковка gzip/deflate
        if (contentEncoding?.contains("gzip", true) == true) {
            data = ungzip(data)
        } else if (contentEncoding?.contains("deflate", true) == true) {
            data = undeflate(data)
        }
        // Удаление chunked (если есть Transfer-Encoding: chunked)
        if (transferEncoding?.contains("chunked", true) == true) {
            data = unchunk(data)
        }
        // Определяем кодировку строго: windows-1251 по умолчанию, иначе из Content-Type или <meta>
        val charset = detectStrictCharset(data, contentType)
        var result = String(data, charset)
        // Fallback: если результат не похож на HTML, пробуем распаковать как gzip/deflate и декодировать windows-1251
        if (result.isBlank() || !result.trimStart().startsWith("<")) {
            val tryGzip = try { String(ungzip(bytes), charset) } catch (_: Exception) { "" }
            if (tryGzip.trimStart().startsWith("<")) return tryGzip
            val tryDeflate = try { String(undeflate(bytes), charset) } catch (_: Exception) { "" }
            if (tryDeflate.trimStart().startsWith("<")) return tryDeflate
        }
        return result
    }

    // Строгое определение кодировки: windows-1251 по умолчанию, иначе из Content-Type или <meta>
    private fun detectStrictCharset(data: ByteArray, contentType: String?): Charset {
        // 1. Из заголовка Content-Type
        val ct = contentType?.lowercase()
        if (ct != null && ct.contains("charset=")) {
            val cs = ct.substringAfter("charset=").substringBefore(';').trim()
            try { return Charset.forName(cs) } catch (_: Exception) {}
        }
        // 2. Из мета-тега в HTML (декодируем windows-1251, как в ABClient)
        val html = String(data, Charset.forName("windows-1251"))
        val meta = Regex("<meta[^>]*charset=([a-zA-Z0-9-]+)", RegexOption.IGNORE_CASE).find(html)
        val cs = meta?.groupValues?.get(1)
        if (cs != null) {
            try { return Charset.forName(cs) } catch (_: Exception) {}
        }
        // 3. Всегда windows-1251 по умолчанию
        return Charset.forName("windows-1251")
    }

    private fun ungzip(data: ByteArray): ByteArray {
        return try {
            GZIPInputStream(ByteArrayInputStream(data)).readBytes()
        } catch (e: Exception) {
            data
        }
    }

    private fun undeflate(data: ByteArray): ByteArray {
        return try {
            InflaterInputStream(ByteArrayInputStream(data)).readBytes()
        } catch (e: Exception) {
            data
        }
    }

    // Удаление chunked transfer encoding (простая реализация)
    private fun unchunk(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        var i = 0
        while (i < data.size) {
            // Поиск \r (13) начиная с позиции i
            var lineEnd = -1
            for (k in i until data.size) {
                if (data[k] == 13.toByte()) { // \r
                    lineEnd = k
                    break
                }
            }
            if (lineEnd == -1) break
            val chunkSizeStr = String(data, i, lineEnd - i)
            val chunkSize = chunkSizeStr.trim().toIntOrNull(16) ?: break
            i = lineEnd + 2 // skip \r\n
            if (chunkSize == 0) break
            if (i + chunkSize > data.size) break
            for (j in 0 until chunkSize) {
                result.add(data[i + j])
            }
            i += chunkSize + 2 // skip chunk + \r\n
        }
        return result.toByteArray()
    }
}
