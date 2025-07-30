package com.metacomputing.namespring.utils

import android.util.Log

object StringUtils {
    // TODO put some here
}

fun CharSequence.underscoreIfEmpty(): String {
    return if (isEmpty()) "_"
    else toString().toLetterList().joinToString("") { it.ifEmpty { "_" } }
}

fun String.emptyIfUnderscore(): String {
    return toLetterList().joinToString("") { if (it == "_") "" else it }
}

fun String.getHanjaAt(index: Int): String {
    val letters = toLetterList()
    return if (letters.size > index) letters[index] else ""
}

fun String.toLetterList(): ArrayList<String> {
    return codePoints().toArray().map { Character.toChars(it).concatToString() }.toCollection(ArrayList())
}

fun String.appendOrReplaceHanja(index: Int, hanja: String): String {
    with(toLetterList()) {
        if (hanja.isNotEmpty()) {
            val newChar = hanja.toLetterList()[0]

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