package com.metacomputing.namespring.utils

import android.util.Log

object StringUtils {
    // TODO put some here
}

fun String.getHanjaAt(index: Int): String {
    val strings = toHanjaList()
    return if (strings.size > index) strings[index] else ""
}

fun String.toHanjaList(): ArrayList<String> {
    return codePoints().toArray().map { Character.toChars(it).concatToString() }.toCollection(ArrayList())
}

fun String.appendOrReplaceHanja(index: Int, hanja: String): String {
    with(toHanjaList()) {
        if (hanja.isNotEmpty()) {
            val newChar = hanja.toHanjaList()[0]

            if (size > index) set(index, newChar)
            else if (size == index) add(newChar)
            else throw RuntimeException("Invalid index. required $index index from the String '$this'")
            return joinToString("")
        }
        else return run {
            removeAt(size - 1)
            joinToString("")
        }
    }
}