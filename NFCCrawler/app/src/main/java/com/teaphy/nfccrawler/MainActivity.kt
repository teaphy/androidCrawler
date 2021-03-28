package com.teaphy.nfccrawler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teaphy.nfccrawler.nfc.NFCActivity
import com.teaphy.nfccrawler.reader.NfcReaderActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun gotoNFC(view: View) {
        startActivity(Intent(this, NFCActivity::class.java))
    }

    fun gotoNFCReader(view: View) {
        startActivity(Intent(this, NfcReaderActivity::class.java))
    }
}