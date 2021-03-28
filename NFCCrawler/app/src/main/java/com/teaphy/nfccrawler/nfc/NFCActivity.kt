package com.teaphy.nfccrawler.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.Bundle
import android.os.Message
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.teaphy.nfccrawler.R
import com.teaphy.nfccrawler.util.NdefRecordUtil
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.experimental.and

class NFCActivity : AppCompatActivity() {

    companion object {
        const val CODE_FOREGROUND_NFC = 0X01
    }

    private lateinit var resultText: TextView

    private val mNfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    // 用于声明前台调度系统所需的PendingIntent、IntentFilter及tech数组
    private var mPendingIntent: PendingIntent? = null
    private var mNdefFilterArray: Array<IntentFilter>? = null
    private var mTechListArray: Array<Array<String>>? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // NFC前台调度系统 --> 4
        // 处理扫描到的NFC标签的数据
        parseNFCResult(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_n_f_c)

        initView()

        // NFC前台调度系统 --> 1
        initForeground()
    }

    override fun onResume() {
        super.onResume()

        // NFC前台调度系统 --> 2
        // NFC前台调度系统
        // enableForegroundDispatch()方法必须在主线程中被调用，并且被Activity在前台
        // 必须实现onNewIntent()方法来处理扫描到的NFC标签的数据
        mNfcAdapter.enableForegroundDispatch(
                this,
                mPendingIntent,
                mNdefFilterArray,
                mTechListArray
        )

        parseNFCResult(intent)
    }

    override fun onPause() {
        super.onPause()
        // NFC前台调度系统 --> 3
        // 禁用NFC前台调度系统
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

                            val record = parseWellKnownTextRecord(ndefRecord)
                            Log.e("teaphy", "record: $record")
                        }
                    }

                    processNdefMessage(messages)

                    resultText.text = Arrays.toString(messages)
                }
            }
        }
    }

    private fun processNdefMessage(messages: Array<NdefMessage?>?) {
        if (null == messages || messages.isEmpty()) {
            Log.e(javaClass.name, "${javaClass.name}: NdefMessage is null")
            return
        }

        var size: Int = 0
        var records: Array<NdefRecord>

        messages.forEachIndexed { index, ndefMessage ->
            size = ndefMessage?.records?.size ?: 0
            Log.e(javaClass.name, "${javaClass.name}: NdefMessage - $index - size: $size")
            records = ndefMessage?.records ?: arrayOf()
            processNdefRecord(records, index)
        }
    }

    private fun processNdefRecord(records: Array<NdefRecord>?, indexMessage: Int) {
        if (null == records || records.isEmpty()) {
            Log.e(javaClass.name, "${javaClass.name}: NdefRecord is null , index of Message is : $indexMessage")
            return
        }

        records.forEachIndexed { index, ndefRecord ->
            when (ndefRecord.tnf) {
                NdefRecord.TNF_EMPTY -> {
                    Log.e(javaClass.name, "${javaClass.name}: NdefRecord is empty, index : $index")
                }
                NdefRecord.TNF_WELL_KNOWN -> {
                    when {
                        Arrays.equals(ndefRecord.type, NdefRecord.RTD_URI) -> {
                            Log.e(javaClass.name, "${javaClass.name}: NdefRecord：${ndefRecord.toUri()}, index : $index")
                        }
                        Arrays.equals(ndefRecord.type, NdefRecord.RTD_TEXT) -> {
                            Log.e(javaClass.name, "${javaClass.name}: NdefRecord：${ndefRecord.toMimeType()}, index : $index")
                        }
                        else -> {
                            Log.e(javaClass.name, "${javaClass.name}: NdefRecord 要回退了, index : $index")
                        }
                    }
                }
                NdefRecord.TNF_ABSOLUTE_URI -> {
                    Log.e(javaClass.name, "${javaClass.name}: index : $index, uri: ${ndefRecord.toUri()?.path ?: ""}")
                }
                NdefRecord.TNF_EXTERNAL_TYPE -> {
                    Log.e(javaClass.name, "${javaClass.name}: NdefRecord is TNF_EXTERNAL_TYPE, index : $index")
                }
                NdefRecord.TNF_MIME_MEDIA -> {
                    Log.e(javaClass.name, "${javaClass.name}: NdefRecord is TNF_MIME_MEDIA, index : $index")
                }
                NdefRecord.TNF_UNCHANGED -> {
                    Log.e(javaClass.name, "${javaClass.name}: NdefRecord is TNF_UNCHANGED, index : $index")
                }
                NdefRecord.TNF_UNKNOWN -> {
                    Log.e(javaClass.name, "${javaClass.name}: NdefRecord is TNF_UNKNOWN, index : $index")
                }
            }
        }
    }


    // 声明NFC前台调度系统Filter
    private fun initForeground() {
        // 1. 创建一个PendingIntent对象，以便Android系统能够在扫描到NFC标签时，用它来封装NFC标签的详细信息
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        mPendingIntent = PendingIntent.getActivity(this, CODE_FOREGROUND_NFC, intent, 0)

        // 2. 声明想要拦截处理的Intent对象的Intent过滤器。前台调度系统会在设备扫描到NFC标签时，用声明的Intent过滤器来
        //    检查接收到的Intent对象。如果匹配就会让应用程序来处理这个Intent对象；如果不匹配，前台调度系统会回退到Intent
        //    调度系统。如果Intent过滤器和tech过滤器的数组指定了null，那么说明要所有的过滤回退到TAG_DISCOVERED类型的
        //    Intent对象的标签。
        val ndefFilter: IntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        // 处理所有的MMIME
        ndefFilter.addDataType("*/*")
        mNdefFilterArray = arrayOf(ndefFilter)

        // 3. 创建一个NFC标签tech的数组.
        // 通过调用Object.class.getName()方法获取想要获取想要支持的tech的类
        mTechListArray = arrayOf(arrayOf(NfcF::class.java.name))
    }

    // 解析NdefRecord.TNF_WELL_KNOWN格式的RTD_TEXT类型
    public fun parseWellKnownTextRecord(record: NdefRecord?): String? {
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

            //下面开始NDEF文本数据第一个字节，获取状态字节编码
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

    // 解析TNF_ABSOLUTE_URI
    public fun parseAbsoluteUriRecord(record: NdefRecord?): Uri? {
        if (null == record) return null
        // 获取记录的payload
        val payload: ByteArray = record.payload
        return try {
            // 将payload转为String
            val uri = Uri.parse(String(payload, StandardCharsets.UTF_8))
            uri.normalizeScheme()
        } catch (e: Exception) {
            null
        }
    }

    // defRecord.TNF_WELL_KNOWN格式的RTD_URI类型
    public fun parseWellKnownUriRecord(record: NdefRecord?): Uri? {
        return parseWellKnownUriRecord(record, false)
    }

    public fun parseWellKnownUriRecord(record: NdefRecord?, inSmartPoster: Boolean): Uri? {
        if (null == record) return null

        // 判断record为类型是否为NdefRecord.RTD_URI，如果不是抛出异常
        if (!Arrays.equals(record.type, NdefRecord.RTD_URI)) {
            throw IllegalArgumentException("The type of Record is't NdefRecord.RTD_URI")
        }

        val type = record.type
        // 获取记录的payload
        val payload = record.payload


        if (Arrays.equals(type, NdefRecord.RTD_SMART_POSTER) && !inSmartPoster) {
            try {
                // check payload for a nested NDEF Message containing a URI
                val nestedMessage = NdefMessage(payload)
                for (nestedRecord in nestedMessage.records) {
                    val uri: Uri? = parseWellKnownUriRecord(nestedRecord, true)
                    if (uri != null) {
                        return uri
                    }
                }
            } catch (e: FormatException) {
            }
        } else if (Arrays.equals(type, NdefRecord.RTD_URI)) {
            val wktUri: Uri? = parseWktUri(payload)
            return wktUri?.normalizeScheme()
        }

        return null
    }

    private fun parseWktUri(payload: ByteArray): Uri? {
        if (payload.size < 2) {
            return null
        }

        // 获取URi的前缀
        val prefixIndex: Int = (payload[0] and 0xFF.toByte()).toInt()
        if (prefixIndex < 0 || prefixIndex >= NdefRecordUtil.URI_PREFIX_MAP.size) {
            return null
        }
        val prefix = NdefRecordUtil.URI_PREFIX_MAP[prefixIndex]

        val suffix = String(payload.copyOfRange(1, payload.size),
                StandardCharsets.UTF_8)
        return Uri.parse(prefix + suffix)
    }
}