package com.teaphy.blecrawler

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.ParcelUuid
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils


class MainActivity : AppCompatActivity() {

    private lateinit var enableTipText: TextView
    private lateinit var enableSwitch: SwitchCompat

    private lateinit var nameLayout: ConstraintLayout
    private lateinit var nameValueText: TextView

    private lateinit var scanText: TextView
    private lateinit var stopScanText: TextView
    private lateinit var countdownText: TextView

    private lateinit var pairedRecyclerView: RecyclerView
    private lateinit var availableRecyclerView: RecyclerView

    private lateinit var deviceContentLayout: LinearLayout

    private lateinit var bluetoothState: BluetoothState

    private lateinit var bleMonitorReceiver: BluetoothMonitorReceiver

    private var bluetoothName: String? = null

    companion object {
        private const val BLUETOOTH_ENABLE_CODE = 0x01
    }

    // 获取蓝牙适配器
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    // 用于请求定位权限
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (null != it && it) {
                doInitBluetooth()
            } else {
                ToastUtils.showShort("该应用需要定位权限")
            }
        }

    // 获取扫描结果的回调
    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        Log.e("teaphy", "leScanCallback - name: ${device.name}, rssi: $rssi")
    }

    private val scannerScanCallback = object : ScanCallback() {

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)

            results?.apply {
                val list = filter {
                    !TextUtils.isEmpty(it.device?.name)
                }.map {
                    it.device
                }

                runOnUiThread {
                    deviceAdapter.addData(list)
                }
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            Log.e(
                "teaphy", "scannerScanCallback - callbackType: $callbackType, " +
                        "name: ${result?.device?.name}, alia: ${result?.device?.alias}，" +
                        " address: ${result?.device?.address}， " +
                        "class: ${result?.device?.bluetoothClass}, class - major: ${result?.device?.bluetoothClass?.majorDeviceClass}"
            )
            result!!.device!!.bluetoothClass.deviceClass
            if (!TextUtils.isEmpty(result?.device?.name)) {
                runOnUiThread {
                    deviceAdapter.addData(result!!.device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            ToastUtils.showShort("扫描失败")
        }
    }

    private var bluetoothLeScanner: BluetoothLeScanner? = null

    private var isScan = false

    // 倒计时
    private val countdownTotal = 60_000L
    private val countdownInterval  = 1000L
    val countDownTimer = object : CountDownTimer(countdownTotal, countdownInterval) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            val time = millisUntilFinished / 1000
            countdownText.text = "${time}S"
        }

        override fun onFinish() {
            stopScan()
        }
    }

    // UI
    private val deviceAdapter: DeviceAdapter by lazy {
        DeviceAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerBleMonitorReceiver()

        initView()

        initBluetooth()

        setListener()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bleMonitorReceiver)
    }

    private fun initView() {
        enableTipText = findViewById(R.id.enable_tip_text)
        enableSwitch = findViewById(R.id.enable_switch)

        nameLayout = findViewById(R.id.name_layout)
        nameValueText = findViewById(R.id.name_value_text)

        scanText = findViewById(R.id.scan_text)
        stopScanText = findViewById(R.id.stop_scan_text)
        countdownText = findViewById(R.id.countdown_text)

        pairedRecyclerView = findViewById(R.id.paired_recycler_view)
        availableRecyclerView = findViewById(R.id.available_recycler_view)

        deviceContentLayout = findViewById(R.id.device_content_layout)

        with(availableRecyclerView) {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = deviceAdapter
        }
    }

    private fun setListener() {
        enableSwitch.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                openBluetooth()
            } else {
                closeBluetooth()
            }
        }

        scanText.setOnClickListener {
            when (bluetoothState) {
                BluetoothState.nonsupport -> ToastUtils.showShort("当前设备不支持蓝牙")
                BluetoothState.disabled -> ToastUtils.showShort("当前蓝牙已关闭")
                BluetoothState.enable -> {
                    startScan()
                }
            }
        }

        stopScanText.setOnClickListener {
            stopScan()
        }
    }


    private fun registerBleMonitorReceiver() {
        bleMonitorReceiver = BluetoothMonitorReceiver()
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(bleMonitorReceiver, filter)
    }

    private fun initBluetooth() {

        val locationPermissionStr = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            Manifest.permission.ACCESS_FINE_LOCATION
        } else {
            Manifest.permission.ACCESS_COARSE_LOCATION
        }

        locationPermissionRequest.launch(locationPermissionStr)

    }

    private fun doInitBluetooth() {

        bluetoothState = isBluetoothEnable()

        bluetoothAdapter?.apply {
            bluetoothName = name
            enableSwitch.isChecked = isEnabled
        }



        resetUI()
    }

    /**
     * 判断当前设备的蓝牙是否可用
     */
    private fun isBluetoothEnable(): BluetoothState {

        return when {
            null == bluetoothAdapter -> BluetoothState.nonsupport
            bluetoothAdapter!!.isEnabled -> BluetoothState.enable
            else -> BluetoothState.disabled
        }
    }


    /**
     * 打开蓝牙
     */
    private fun openBluetooth() {
        // 弹出对话框，打开蓝牙
//        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//        startActivityForResult(intent, RESULT_OK)

        // 隐式打开蓝牙
        bluetoothAdapter?.enable()
    }

    /**
     * 关闭蓝牙
     */
    private fun closeBluetooth() {
        // 跳转到蓝牙设置页面，关闭蓝牙，没有发现弹出对话框关闭蓝牙的
        // val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        // startActivityForResult(intent, RESULT_OK)

        // 隐式关闭蓝牙
        bluetoothAdapter?.disable()
    }

    /**
     * 打开蓝牙扫描
     */
    private fun startScan() {
        if (!isScan) {
            isScan = true
            deviceAdapter.resetData()
            ToastUtils.showShort("开启扫描")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
                bluetoothLeScanner?.startScan(buildScanFilters(), buildScanSettings(), scannerScanCallback)
            } else {
                bluetoothAdapter?.startLeScan(leScanCallback)
            }
//            bluetoothAdapter?.startDiscovery()

            // 找到所需设备后，立即停止扫描
            // 绝对不进行循环扫描，并设置扫描时间限制
            countDownTimer.start()
        }
    }

    /**
     * 停止蓝牙扫描
     */
    private fun stopScan() {
        if (isScan) {
            isScan = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothLeScanner?.stopScan(scannerScanCallback)
                bluetoothLeScanner = null
            } else {
                bluetoothAdapter?.stopLeScan(leScanCallback)
            }
//            bluetoothAdapter?.cancelDiscovery()

            ToastUtils.showShort("结束扫描")
            countDownTimer.cancel()
        }
    }

    // Android 8.1及以上系统后台模式无法开启扫描问题
    // 解决方案：在Android 8.1及以上系统中在后台模式中开启扫描必须要关联扫描过滤器，如此才能在后台模式下完美运行

    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilterList = mutableListOf<ScanFilter>()
        // 通过服务 uuid 过滤自己要连接的设备   过滤器搜索GATT服务UUID
//        val scanFilterBuilder = ScanFilter.Builder()
//        val parcelUuidMask = ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF")
//        val parcelUuid = ParcelUuid.fromString("00001800-0000-1000-8000-00805f9b34fb")
//        scanFilterBuilder.setServiceUuid(parcelUuid, parcelUuidMask)
//        scanFilterList.add(scanFilterBuilder.build())
        return scanFilterList
    }

    private fun buildScanSettings(): ScanSettings? {
        val scanSettingBuilder = ScanSettings.Builder()
        with(scanSettingBuilder) {
            //设置蓝牙LE扫描的扫描模式。
            //使用最高占空比进行扫描。建议只在应用程序处于此模式时使用此模式在前台运行
            setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
            //在主动模式下，即使信号强度较弱，hw也会更快地确定匹配.在一段时间内很少有目击/匹配。
            setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            //设置蓝牙LE扫描的回调类型
            //为每一个匹配过滤条件的蓝牙广告触发一个回调。如果没有过滤器是活动的，所有的广告包被报告
            setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        }

        return scanSettingBuilder.build()
    }


    private fun resetUI() {
        resetBluetoothUI()

        resetBluetoothEnableUI()
    }

    private fun resetBluetoothUI() {
        nameValueText.text = bluetoothName
    }

    private fun resetBluetoothEnableUI() {
        when (bluetoothState) {
            BluetoothState.nonsupport,
            BluetoothState.disabled -> {
                enableTipText.visibility = View.GONE
                nameLayout.isEnabled = false
                deviceContentLayout.visibility = View.GONE
            }
            BluetoothState.enable -> {
                enableTipText.visibility = View.VISIBLE
                nameLayout.isEnabled = true
                deviceContentLayout.visibility = View.VISIBLE
            }
        }
    }

    // 监听蓝牙的状态变化
    class BluetoothMonitorReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                when (action) {
                    // 蓝牙状态变化
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val blueState = getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                        when (blueState) {
                            // 蓝牙正在打开
                            BluetoothAdapter.STATE_TURNING_ON -> {
                                Log.e("teaphy", "蓝牙正在打开")
                            }
                            // 蓝牙已经打开
                            BluetoothAdapter.STATE_ON -> {
                                Log.e("teaphy", "蓝牙已经打开")

                            }
                            // 蓝牙正在关闭
                            BluetoothAdapter.STATE_TURNING_OFF -> {
                                Log.e("teaphy", "蓝牙正在关闭")
                            }
                            // 蓝牙已经关闭
                            BluetoothAdapter.STATE_OFF -> {
                                Log.e("teaphy", "蓝牙已经关闭")
                            }
                        }
                    }
                    // 蓝牙设备已连接
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        Log.e("teaphy", "蓝牙设备已连接")
                    }
                    // 蓝牙设备已断开连接
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        Log.e("teaphy", "蓝牙设备已断开连接")
                    }
                    // 发现设备
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as? BluetoothDevice
                        val deviceName = device?.name
                        val deviceHardwareAddress = device?.address // MAC address
                        Log.e("teaphy", "deviceName: $deviceName, address: $deviceHardwareAddress")
                    }

                }
            }
        }

    }
}