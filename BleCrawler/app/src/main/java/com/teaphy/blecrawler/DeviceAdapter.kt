package com.teaphy.blecrawler

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * Create by: teaphy
 * Date: 4/5/21
 * Time: 11:44 AM
 */
class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {
    private val list = mutableListOf<BluetoothDevice>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val typeImage: ImageView = itemView.findViewById(R.id.type_image)
        val nameText: TextView = itemView.findViewById(R.id.name_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val bluetoothDevice = list[position]
            nameText.text = bluetoothDevice.alias
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addData(bluetoothDevice: BluetoothDevice) {
        if (!list.contains(bluetoothDevice)) {
            val size = list.size
            list.add(bluetoothDevice)
            notifyItemInserted(size)
        }
    }


    fun addData(bluetoothDevices: List<BluetoothDevice>) {

        if (bluetoothDevices.isEmpty()) {
            return
        }

        bluetoothDevices.forEach {
            addData(it)
        }
    }

    fun resetData() {
        if (list.isNotEmpty()) {
            list.clear()
            notifyDataSetChanged()
        }
    }
}