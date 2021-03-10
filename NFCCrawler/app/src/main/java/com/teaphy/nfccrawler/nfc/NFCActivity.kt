package com.teaphy.nfccrawler.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.teaphy.nfccrawler.R
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

class NFCActivity : AppCompatActivity() {

    private lateinit var resultText: TextView

    private val mNfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    private val mPendingIntent: PendingIntent by lazy {
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        PendingIntent.getActivity(this, 0, intent, 0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseNFCResult(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_n_f_c)

        initView()
    }

    override fun onResume() {
        super.onResume()


        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")    /* Handles all MIME based dispatches.
                                     You should specify only the ones that you need. */
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }

        val intentFiltersArray = arrayOf(ndef)

        val techListsArray = arrayOf(arrayOf<String>(NfcF::class.java.name))

        mNfcAdapter.enableForegroundDispatch(
            this,
            mPendingIntent,
            intentFiltersArray,
            techListsArray
        )

        parseNFCResult(intent)
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter.disableForegroundDispatch(this)
    }

    private fun initView() {
        resultText = findViewById(R.id.result_text)
    }

    private fun parseNFCResult(intent: Intent?) {
        Log.e("teaphy", "parseNFCResult：$intent")
        intent?.apply {
            when (action) {
                NfcAdapter.ACTION_NDEF_DISCOVERED,
                NfcAdapter.ACTION_TAG_DISCOVERED,
                NfcAdapter.ACTION_TECH_DISCOVERED -> {
                    // 用来保存读取到的Tag信息
                    var messages: Array<NdefMessage?>? = null

                    // 通过getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)获取Tag中的信息
                    // 同时将其保存到序列化数组Array<Parcelable>
                    val rawMsg: Array<Parcelable>? =
                        getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                    // 当rawMsg不为null时表明读取到了Tag的信息。
                    rawMsg?.apply {
                        messages = arrayOfNulls<NdefMessage?>(size)
                        forEachIndexed { index, msg ->
                            Log.e("teaphy", "Tag_ASSIST：$msg")
                            messages!![index] = msg as NdefMessage
                        }
                    } ?: let {
                        // 当rawMsg为null时，表明其中包含的是一个未知信息的Tag。
                        val empty = byteArrayOf()
                        // 将message设置为NdefRecord.TNF_UNKNOWN类型的NDEF消息
                        val record: NdefRecord =
                            NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty)
                        val msg: NdefMessage = NdefMessage(arrayOf(record))
                        messages = arrayOf(msg)
                    }

                    messages?.forEach {
                        it?.records?.forEach { ndefRecord ->
                            val record = parseTextRecord(ndefRecord)
                            Log.e("teaphy", "record: $record")
                        }
                    }

                    resultText.text = Arrays.toString(messages)
                }
            }
        }
    }

    public fun parseTextRecord(record: NdefRecord?): String? {
        if (null == record || NdefRecord.TNF_WELL_KNOWN != record.tnf) {
            return null
        }

        // 判断可变长度
        if (!Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
            return null
        }

        try {
            // 获得字节数组，然后进行分析
            val payload = record.payload

            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            val textEncoding =
                if ((payload[0] and 0x80.toByte()).toInt() == 0) "UTF-8" else "UTF-16"

            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            val languageCodeLength: Int = (payload[0] and 0x3f).toInt()

            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            val languageCode = String(payload, 1, languageCodeLength, Charset.forName("US-ASCII"))
            //下面开始NDEF文本数据后面的字节，解析出文本
            //下面开始NDEF文本数据后面的字节，解析出文本
            val textRecord = String(
                payload, languageCodeLength + 1,
                payload.size - languageCodeLength - 1, Charset.forName(textEncoding)
            )
            return textRecord
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
    }
}