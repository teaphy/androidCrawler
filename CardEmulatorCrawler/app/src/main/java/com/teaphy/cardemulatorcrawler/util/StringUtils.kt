package com.teaphy.cardemulatorcrawler.util

import kotlin.experimental.and

/**
 *
 * Create by: teaphy
 * Date: 3/28/21
 * Time: 4:06 PM
 */
class StringUtils {
    companion object {

        fun byteToHex(num: Byte, upper: Boolean): String {
            val hexDigits = CharArray(2)
            if (upper) {
                hexDigits[0] = Character.toUpperCase(Character.forDigit((num.toInt() shr 4) and 0xF, 16))
                hexDigits[1] = Character.toUpperCase(Character.forDigit((num.toInt() and 0xF), 16))
            } else {
                hexDigits[0] = Character.forDigit((num.toInt() shr 4) and 0xF, 16)
                hexDigits[1] = Character.forDigit((num.toInt() and 0xF), 16)
            }
            return String(hexDigits)
        }

        fun encodeHexString(byteArray: ByteArray, upper: Boolean): String {
            val hexStringBuffer = StringBuilder()
            for (aByteArray in byteArray) {
                hexStringBuffer.append(byteToHex(aByteArray, upper))
            }
            return hexStringBuffer.toString()
        }

        fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                        + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }
    }
}