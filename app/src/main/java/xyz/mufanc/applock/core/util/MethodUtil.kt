package xyz.mufanc.applock.core.util

import java.lang.reflect.Method

fun Class<*>.signature(): String {
    return java.lang.reflect.Array.newInstance(this, 0)
        .javaClass.name.replace('.', '/').substring(1)
}

fun Method.signature(): String {
    val builder = StringBuilder()

    builder.append(declaringClass.name).append("#").append(name).append("(")

    this.parameterTypes.forEach { cl ->
        builder.append(cl.signature())
    }

    builder.append(")")
    builder.append(if (returnType == Void.TYPE) "V" else returnType.signature())

    return builder.toString()
}
