package com.example.blescanner.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.blescanner.data.model.BleDevice
import com.example.blescanner.databinding.LayoutBleDeviceItemBinding

class DeviceListAdapter :
    ListAdapter<BleDevice, DeviceListAdapter.DeviceViewHolder>(DeviceDiffUtil()) {

    private lateinit var binding: LayoutBleDeviceItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        binding =
            LayoutBleDeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeviceViewHolder(binding: LayoutBleDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bleDevice: BleDevice) {
            binding.bleDevice = bleDevice
            binding.executePendingBindings()
        }
    }


    class DeviceDiffUtil : DiffUtil.ItemCallback<BleDevice>() {
        override fun areItemsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(oldItem: BleDevice, newItem: BleDevice): Boolean {
            return oldItem == newItem
        }

    }
}

