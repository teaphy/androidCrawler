package com.teaphy.nfccrawler.util

import android.net.Uri
import android.nfc.NdefRecord
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 *
 * Create by: teaphy
 * Date: 3/16/21
 * Time: 5:09 PM
 */
class NdefRecordUtil {
    companion object {

        val URI_PREFIX_MAP = arrayOf(
                "",  // 0x00
                "http://www.",  // 0x01
                "https://www.",  // 0x02
                "http://",  // 0x03
                "https://",  // 0x04
                "tel:",  // 0x05
                "mailto:",  // 0x06
                "ftp://anonymous:anonymous@",  // 0x07
                "ftp://ftp.",  // 0x08
                "ftps://",  // 0x09
                "sftp://",  // 0x0A
                "smb://",  // 0x0B
                "nfs://",  // 0x0C
                "ftp://",  // 0x0D
                "dav://",  // 0x0E
                "news:",  // 0x0F
                "telnet://",  // 0x10
                "imap:",  // 0x11
                "rtsp://",  // 0x12
                "urn:",  // 0x13
                "pop:",  // 0x14
                "sip:",  // 0x15
                "sips:",  // 0x16
                "tftp:",  // 0x17
                "btspp://",  // 0x18
                "btl2cap://",  // 0x19
                "btgoep://",  // 0x1A
                "tcpobex://",  // 0x1B
                "irdaobex://",  // 0x1C
                "file://",  // 0x1D
                "urn:epc:id:",  // 0x1E
                "urn:epc:tag:",  // 0x1F
                "urn:epc:pat:",  // 0x20
                "urn:epc:raw:",  // 0x21
                "urn:epc:",  // 0x22
                "urn:nfc:")

        @JvmStatic
        fun createTextRecord(payload: String, local: Locale, encodeInUtf8: Boolean): NdefRecord {
            val data: ByteArray = convertPayloadData(local, encodeInUtf8, payload)
            // tnf、type、id、payload
            return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), data)
        }

        @JvmStatic
        fun createUriRecord(uri: String): NdefRecord {
            return NdefRecord.createUri(uri)
        }

        @JvmStatic
        fun createUriRecord(uri: Uri): NdefRecord {
            return NdefRecord.createUri(uri)
        }

        @JvmStatic
        fun createUriRecordManual(uri: String, uriPrefix: Byte): NdefRecord {
            val uriField = uri.toByteArray(StandardCharsets.US_ASCII)
            val payload = ByteArray(uriField.size + 1)
            // 设置prefixes, 比如，uriPrefix为0x01时，表示 http://www.
            // 对照表详看NdefRecord中的URI_PREFIX_MAP
            payload[0] = uriPrefix
            System.arraycopy(uriField, 0, payload, 1, uriField.size)
            return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, ByteArray(0), payload)
        }

        @JvmStatic
        fun createAbsoluteUriRecord(uri: String): NdefRecord {
            return ByteArray(0).let { emptyArray ->
                NdefRecord(
                        NdefRecord.TNF_ABSOLUTE_URI,
                        uri.toByteArray(StandardCharsets.US_ASCII),
                        emptyArray,
                        emptyArray
                )
            }
        }

        /**
         * @param domain 通常为App的包名
         */
        @JvmStatic
        fun createExternalRecord(payload: String,
                                 domain: String,
                                 type: String,
                                 local: Locale,
                                 encodeInUtf8: Boolean): NdefRecord {
            val data: ByteArray = convertPayloadData(local, encodeInUtf8, payload)
            return NdefRecord.createExternal(domain, type, data)
        }

        /**
         * @param domain 通常为App的包名
         */
        @JvmStatic
        fun createExternalRecordManual(payload: String,
                                       domain: String,
                                       type: String,
                                       local: Locale,
                                       encodeInUtf8: Boolean): NdefRecord {
            val data: ByteArray = convertPayloadData(local, encodeInUtf8, payload)
            return NdefRecord(
                    NdefRecord.TNF_EXTERNAL_TYPE,
                    "$domain:$type".toByteArray(StandardCharsets.US_ASCII),
                    ByteArray(0),
                    data
            )
        }

        @JvmStatic
        fun createMimeRecord(mimeType: String, payload: String): NdefRecord {
            val mimeDta = payload.toByteArray(StandardCharsets.US_ASCII)
            return NdefRecord.createMime(mimeType, mimeDta)
        }

        @JvmStatic
        fun createMimeRecordManual(mimeType: String, payload: String): NdefRecord {
            val mimeDta = payload.toByteArray(StandardCharsets.US_ASCII)
            return NdefRecord(
                    NdefRecord.TNF_MIME_MEDIA,
                    mimeType.toByteArray(StandardCharsets.US_ASCII),
                    ByteArray(0),
                    mimeDta
            )
        }

        private fun convertPayloadData(local: Locale, encodeInUtf8: Boolean, payload: String): ByteArray {
            val langBytes: ByteArray = local.language.toByteArray(StandardCharsets.US_ASCII)
            val utfEncoding: Charset = if (encodeInUtf8) {
                Charset.forName("UTF-8")
            } else {
                Charset.forName("UTF-16")
            }
            val textBytes: ByteArray = payload.toByteArray(utfEncoding)

            val utfBit = if (encodeInUtf8) {
                0
            } else {
                // Java中的左移位 <<
                1 shl 7
            }

            val status: Byte = (utfBit + langBytes.size).toByte()

            val dataSize = 1 + langBytes.size + textBytes.size
            val data: ByteArray = ByteArray(dataSize)

            data[0] = status
            System.arraycopy(langBytes, 0, data, 1, langBytes.size)
            System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
            return data
        }

        @JvmStatic
        fun createApplicationRecord(packageName: String): NdefRecord {
            return NdefRecord.createApplicationRecord(packageName)
        }
    }
}