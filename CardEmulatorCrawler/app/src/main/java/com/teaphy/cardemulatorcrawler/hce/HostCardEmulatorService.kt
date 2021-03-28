package com.teaphy.cardemulatorcrawler.hce

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.teaphy.cardemulatorcrawler.util.StringUtils

/**
 * 卡模拟服务
 * 要使用基于主机的卡模拟来模拟 NFC 卡，需要创建用于处理 NFC 交易的 Service 组件。
 * Create by: teaphy
 * Date: 3/28/21
 * Time: 3:49 PM
 */
class HostCardEmulatorService : HostApduService() {

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

        const val MIN_APDU_LENGTH = 12
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("teaphy", "processCommandApdu")
    }

    // 只要 NFC 读取器向HEC服务发送应用协议数据单元 (APDU)，系统就会调用 processCommandApdu()
    // APDU 也在 ISO/IEC 7816-4 规范中定义。APDU 是在 NFC 读取器和HCE 服务之间进行交换的应用级
    // 数据包。该应用级协议为半双工：NFC 读取器会向MyHostApduService发送命令 APDU，反之它会等待
    // MyHostApduService发送响应 APDU。
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {


        Toast.makeText(this, "processCommandApdu", Toast.LENGTH_LONG).show()

        if (commandApdu == null) {
            return StringUtils.hexStringToByteArray(STATUS_SUCCESS)
        }


        val hexCommandApdu: String = StringUtils.encodeHexString(commandApdu, true)


        Log.e("teaphy", "processCommandApdu: $hexCommandApdu")

        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            return StringUtils.hexStringToByteArray(STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return StringUtils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return StringUtils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        return if (hexCommandApdu.substring(10, 24) == AID)  {
            StringUtils.hexStringToByteArray(STATUS_SUCCESS)
        } else {
            StringUtils.hexStringToByteArray(STATUS_FAILED)
        }
    }

    override fun onDeactivated(reason: Int) {
        Toast.makeText(this, "Deactivated - reason:" + reason, Toast.LENGTH_LONG).show()
    }
}