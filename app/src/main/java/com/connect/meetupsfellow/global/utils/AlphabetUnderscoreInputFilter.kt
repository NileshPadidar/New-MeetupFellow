package com.connect.meetupsfellow.global.utils

import android.text.InputFilter
import android.text.Spanned

class AlphabetUnderscoreInputFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
       /* for (i in start until end) {
            if (!Character.isLetter(source[i]) && source[i] != '_') {
                return ""
            }
        }
        return null
    }*/
        val filteredStringBuilder = StringBuilder()

        // Loop through the source characters from start to end
        for (i in start until end) {
            val currentChar = source[i]
            // Check if the character is a letter, digit, or underscore
            if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                filteredStringBuilder.append(currentChar)
            }
        }

        // If all characters are valid, return null to indicate no changes are necessary
        // If some characters were invalid, return the filtered string
        return if (filteredStringBuilder.length == end - start) {
            // All characters are valid, no need to change anything
            null
        } else {
            // Some characters were invalid, return the filtered string
            filteredStringBuilder.toString()
        }
    }
}