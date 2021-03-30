package com.teaphy.nfccrawler.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.NdefRecord
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.NfcA
import android.nfc.tech.NfcF
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.teaphy.nfccrawler.R
import com.teaphy.nfccrawler.util.StringUtils
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and


class NFCActivity : AppCompatActivity() {

    companion object {
        const val CODE_FOREGROUND_NFC = 0X01
    }

    private lateinit var resultText: TextView

    private val mNfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    // 用于声明前台调度系统所需的PendingIntent、IntentFilter及tech数组
    private var mPendingIntent: PendingIntent? = null
    private var mNdefFilterArray: Array<IntentFilter>? = null
    private var mTechListArray: Array<Array<String>>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_n_f_c)

        initView()

        checkNfcEnable()

        // NFC前台调度系统 --> 1
        initForeground()

        intent?.apply {
            parseNFCResult(this)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // NFC前台调度系统 --> 4
        // 处理扫描到的NFC标签的数据
        intent?.apply {
            parseNFCResult(this)
        }
    }

    override fun onResume() {
        super.onResume()

        // NFC前台调度系统 --> 2
        // NFC前台调度系统
        // enableForegroundDispatch()方法必须在主线程中被调用，并且被Activity在前台
        // 必须实现onNewIntent()方法来处理扫描到的NFC标签的数据
        mNfcAdapter?.enableForegroundDispatch(
            this,
            mPendingIntent,
            mNdefFilterArray,
            mTechListArray
        )
    }

    override fun onPause() {
        super.onPause()
        // NFC前台调度系统 --> 3
        // 禁用NFC前台调度系统
        mNfcAdapter?.disableForegroundDispatch(this)
    }

    private fun initView() {
        resultText = findViewById(R.id.result_text)
    }

    // 判断NFC是否可用
    private fun checkNfcEnable() {
        mNfcAdapter?.apply {
            if (!isEnabled) {
                resultText.text = "Please open NFC"
            }
        } ?: let {
            resultText.text = "Not support NFC!"
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

    private fun parseNFCResult(intent: Intent?) {
        Log.e("teaphy", "parseNFCResult：$intent")
        intent?.apply {
            when (action) {
                NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                    resultText.text = "ACTION_NDEF_DISCOVERED"
                }
                NfcAdapter.ACTION_TAG_DISCOVERED -> {
                    resultText.text = "ACTION_TAG_DISCOVERED"
                }
                NfcAdapter.ACTION_TECH_DISCOVERED -> {


                    val nfcInfo = StringBuilder()

                    // Id
                    val extraId: ByteArray? = getByteArrayExtra(NfcAdapter.EXTRA_ID)

                    extraId?.let {
                        nfcInfo.append("Id (hex): ${StringUtils.encodeHexString(it, false)} \n")
                    } ?: let {
                        nfcInfo.append("Id (hex): null \n")
                    }

                    // Tag
                    val tag: Tag = getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) as Tag

                    // Card type.
                    val cardType = StringBuilder("Card Type: \n")

                    // Technologies
                    val technologiesAvailable = StringBuilder("Technologies Available: \n")

                    // Sector and block.
                    val sectorAndBlock = java.lang.StringBuilder("Storage: \n")

                    // Sector check
                    val sectorCheck = java.lang.StringBuilder("Sector check: \n")

                    // NfcA
                    val nfcAMessage = java.lang.StringBuilder("NfcA: \n")

                    var idx = 0

                    // 获取所支持的所有标签类型
                    val techList = tag.techList

                    for (tech in techList) {
                        when (tech) {
                            MifareClassic::class.java.name -> {
                                handleMafareTech(tag, cardType, sectorCheck)
                            }
                            MifareUltralight::class.java.name -> {
                                // Mifare Ultralight
                                val mful = MifareUltralight.get(tag)
                                when (mful.type) {
                                    MifareUltralight.TYPE_ULTRALIGHT -> cardType.append("Ultralight")
                                    MifareUltralight.TYPE_ULTRALIGHT_C -> cardType.append("Ultralight C")
                                    MifareUltralight.TYPE_UNKNOWN -> cardType.append("Unknown")
                                }
                            }
                            NfcA::class.java.name -> {
                                val nfca = NfcA.get(tag)
                                nfcAMessage
                                    .append(
                                        "ATQA/SENS_RES: ${
                                            StringUtils.encodeHexString(
                                                nfca.atqa,
                                                true
                                            )
                                        } \n"
                                    )
                                    .append("SAK/SEL_RES: ${nfca.sak} \n")

                                // Tag
//                                val tag: Tag = getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) as Tag
//                                val nfcA = NfcA.get(tag)
//                                Thread {
//                                    Log.e("teaphy", "开启NfcA")
//                                    try {
//                                        nfcA.connect()
//
//                                        // READ_CNT命令
//                                        val readCntCmd = byteArrayOf(0x39)
//
//                                        val response = nfcA.transceive(readCntCmd)
//                                        Log.e(
//                                            "teaphy",
//                                            "NfcA Response: ${StringUtils.encodeHexString(response, true)}"
//                                        )
//                                    } catch (e: IOException) {
//                                        Log.e("teaphy", "NfcA Exception： $e")
//                                    } finally {
//                                        nfcA.close()
//                                    }
//
//
//                                }.run()
                            }
                        }

                        val techPkgFields: List<String> = tech.split("\\.")
                        if (techPkgFields.isNotEmpty()) {
                            val techName = techPkgFields[techPkgFields.size - 1]
                            if (0 == idx++) {
                                technologiesAvailable.append(techName)
                            } else {
                                technologiesAvailable.append(", ").append(techName)
                            }
                        }
                    }

                    nfcInfo.append("\n")
                        .append(technologiesAvailable)
                        .append("\n")
                        .append("\n")
                        .append(cardType)
                        .append("\n")

                    // NDEF Message

                    val sbNdefMessages = java.lang.StringBuilder("NDEF Messages: \n")

                    handleNdefMessage(sbNdefMessages)

                    nfcInfo.append("\n")
                        .append(sbNdefMessages)
                        .append("\n")
                        .append("\n")
                        .append(sectorAndBlock)
                        .append("\n")
                        .append("\n")
                        .append(nfcAMessage)
                        .append("\n")
                        .append("\n")
                        .append(sectorCheck)
                        .append("\n")

                    resultText.text = nfcInfo.toString()
                }
            }
        }
    }

    private fun Intent.handleNdefMessage(sbNdefMessages: java.lang.StringBuilder) {
        val rawMessages = getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

        if (rawMessages != null) {
            val messages = arrayOfNulls<NdefMessage>(rawMessages.size)
            for (i in rawMessages.indices) {
                messages[i] = rawMessages[i] as NdefMessage
            }
            for (message in messages) {
                for (record in message!!.records) {
                    if (record.tnf == NdefRecord.TNF_WELL_KNOWN) {
                        if (Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                            try {
                                // NFC Forum "Text Record Type Definition" section 3.2.1.
                                val payload = record.payload
                                val textEncoding =
                                    if ((payload[0] and 128.toByte()).toInt() == 0) "UTF-8" else "UTF-16"
                                val languageCodeLength: Int =
                                    (payload[0] and 63.toByte()).toInt()
                                val languageCode =
                                    String(
                                        payload,
                                        1,
                                        languageCodeLength,
                                        Charset.forName("US-ASCII")
                                    )
                                val text = String(
                                    payload,
                                    languageCodeLength + 1,
                                    payload.size - languageCodeLength - 1,
                                    Charset.forName(textEncoding)
                                )
                                sbNdefMessages.append(" - ")
                                    .append(languageCode)
                                    .append(", ")
                                    .append(textEncoding)
                                    .append(", ")
                                    .append(text)
                                    .append("\n")
                            } catch (e: UnsupportedEncodingException) {
                                // should never happen unless we get a malformed tag.
                                throw IllegalArgumentException(e)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleMafareTech(
        tag: Tag,
        cardType: StringBuilder,
        sectorCheck: java.lang.StringBuilder
    ) {
        // 根据Tag对象来获得MifareClassic对象；
        val mfc: MifareClassic = MifareClassic.get(tag)
        // 卡类型
        // 获得MifareClassic标签的具体类型：TYPE_CLASSIC，TYPE_PLUA，TYPE_PRO，TYPE_UNKNOWN
        when (mfc.type) {
            MifareClassic.TYPE_CLASSIC -> cardType.append("Classic")
            MifareClassic.TYPE_PLUS -> cardType.append("Plus")
            MifareClassic.TYPE_PRO -> cardType.append("Pro")
            MifareClassic.TYPE_UNKNOWN -> cardType.append("Unknown")
        }

        // getSectorCount()：获得标签总共有的扇区数量
        // getBlockCount()：获得标签总共有的的块数量
        // getSize()：获得标签的容量：SIZE_1K,SIZE_2K,SIZE_4K,SIZE_MINI
        with(sectorCheck) {
            append("Sectors: ")
            append(mfc.sectorCount)
            append("\n")
            append("Blocks: ")
            append(mfc.blockCount)
            append("\n")
            append("Size: ")
            append(mfc.size)
            append(" Bytes")
            append("\n")
        }

        try {

            // 允许对MifareClassic标签进行IO操作
            mfc.connect()

            // 获取总扇区数
            for (index in 0.until(mfc.sectorCount)) {
                when {
                    // 验证当前扇区的KeyA密码，返回值为ture或false
                    mfc.authenticateSectorWithKeyA(
                        index,
                        MifareClassic.KEY_DEFAULT
                    ) -> {
                        sectorCheck.append("Sector <")
                            .append(index)
                            .append("> with KeyA auth succ\n")

                        // Read block of sector
                        // 当前扇区的第1块的块号
                        val blockIndex = mfc.sectorToBlock(index)

                        // 获取当前扇区的块数量
                        for (j in 0 until mfc.getBlockCountInSector(index)) {
                            // 读取当前块的数据
                            val blockData = mfc.readBlock(blockIndex + j)
                            // 将数据data写入当前块；注意：data必须刚好是16Byte，末尾不能用0填充，应该用空格
                            // writeBlock（int，data）
                            sectorCheck.append("  Block <")
                                .append(blockIndex + j)
                                .append("> ")
                                .append(
                                    StringUtils.encodeHexString(
                                        blockData,
                                        false
                                    )
                                ).append("\n")
                        }
                    }
                    mfc.authenticateSectorWithKeyB(
                        index,
                        MifareClassic.KEY_DEFAULT
                    ) -> {
                        sectorCheck.append("Sector <").append(index)
                            .append("> with KeyB auth succ\n")

                        // Read block of sector
                        val blockIndex = mfc.sectorToBlock(index)
                        for (j in 0 until mfc.getBlockCountInSector(index)) {
                            val blockData = mfc.readBlock(blockIndex + j)
                            sectorCheck.append("  Block <")
                                .append(blockIndex + j).append("> ")
                                .append(
                                    StringUtils.encodeHexString(
                                        blockData,
                                        false
                                    )
                                ).append("\n")
                        }
                    }
                    else -> {
                        sectorCheck.append("Sector <").append(index)
                            .append("> auth failed\n")
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace();
            Toast.makeText(
                this@NFCActivity,
                "Try again and keep NFC tag below device",
                Toast.LENGTH_LONG
            ).show()
        } finally {
            mfc.close()
        }
    }

}