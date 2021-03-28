package com.teaphy.nfccrawler.nfc

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.teaphy.nfccrawler.R

class TestCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_code)



    }

    private fun checkNfc() {
        // 获取默认的NFC适配器
        val mNfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(this)

        // 如果mNfcAdapter为null，表明设备不支持NFC
        if (null == mNfcAdapter) {

        } else {
            // 判断用户是否开启了NFC
            if (mNfcAdapter.isEnabled) {
                mNfcAdapter.isNdefPushEnabled
            } else {
                // NFC没有开启，跳转到设置页
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            }

            if (mNfcAdapter.isNdefPushEnabled) {

            } else {
                // NFC Beam不可用，跳转到设置页
                startActivity(Intent(Settings.ACTION_NFCSHARING_SETTINGS))
            }
        }
    }
}