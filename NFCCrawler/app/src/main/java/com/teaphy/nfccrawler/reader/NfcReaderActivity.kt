package com.teaphy.nfccrawler.reader

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.teaphy.nfccrawler.R
import com.teaphy.nfccrawler.util.StringUtils
import java.io.IOException


class NfcReaderActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var resultText: TextView

    companion object {
        // 相关的状态
        // 成功
        const val STATUS_SUCCESS = "9000"

        // 失败
        const val STATUS_FAILED = "6F00"


        const val CLA_NOT_SUPPORTED = "6E00"
        const val INS_NOT_SUPPORTED = "6D00"
        const val AID = "A0000002471001"
        const val SELECT_INS = "A4"
        const val DEFAULT_CLA = "00"
        const val LC = "07"
        const val MIN_APDU_LENGTH = 12
    }

    private val mNfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_reader)

        resultText = findViewById(R.id.result_text)
    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter.enableReaderMode(
            this, this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }


    override fun onPause() {
        super.onPause()
        mNfcAdapter.disableReaderMode(this)
    }


    override fun onTagDiscovered(tag: Tag?) {

        Log.e("teaphy", "onTagDiscovered")

        // Card response for IsoDep
        val cardResp = StringBuilder("Card response: \n")

        // read card data of CardEmulator
        val isoDep = IsoDep.get(tag)
        try {
            isoDep.connect()
            val resp =
                isoDep.transceive(StringUtils.hexStringToByteArray(DEFAULT_CLA + SELECT_INS + "0400" + LC + AID))
            val respStatus: String = StringUtils.encodeHexString(resp, true)
            if (respStatus == STATUS_SUCCESS) {
                cardResp.append("Success response")
            } else {
                cardResp.append("Failed response, code:").append(respStatus)
            }

            Log.e("teaphy", "onTagDiscovered cardResp: $cardResp")

            runOnUiThread {
                resultText.text = cardResp.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()

            runOnUiThread {
                resultText.text = e.toString()
            }
        } finally {
            isoDep.close()
        }
    }


}