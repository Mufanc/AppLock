package mufanc.tools.applock.ui.fragment.apps

import java.nio.charset.Charset

object PinyinUtils {

    fun convert(str: String): String {
        return str.lowercase().replace("[\\u4e00-\\u9fa5]".toRegex()) {
            toPinyinChar(it.value)
        }
    }

    private fun toPinyinChar(ch: String): String {
        val ord = ch.toByteArray(Charset.forName("gbk"))
            .map { it.toUByte().toInt() }
            .let { it[0] * 256 + it[1] }
        if (ord < 0xb0a1) return "*"
        if (ord < 0xb0c5) return "a"
        if (ord < 0xb2c1) return "b"
        if (ord < 0xb4ee) return "c"
        if (ord < 0xb6ea) return "d"
        if (ord < 0xb7a2) return "e"
        if (ord < 0xb8c1) return "f"
        if (ord < 0xb9fe) return "g"
        if (ord < 0xbbf7) return "h"
        if (ord < 0xbfa6) return "j"
        if (ord < 0xc0ac) return "k"
        if (ord < 0xc2e8) return "l"
        if (ord < 0xc4c3) return "m"
        if (ord < 0xc5b6) return "n"
        if (ord < 0xc5be) return "o"
        if (ord < 0xc6da) return "p"
        if (ord < 0xc8bb) return "q"
        if (ord < 0xc8f6) return "r"
        if (ord < 0xcbfa) return "s"
        if (ord < 0xcdda) return "t"
        if (ord < 0xcef4) return "w"
        if (ord < 0xd1b9) return "x"
        if (ord < 0xd4d1) return "y"
        if (ord < 0xd7fa) return "z"
        return "*"
    }
}