package com.example.blescanner.ui.main

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.os.ParcelUuid
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.blescanner.data.ble.BleScanner
import com.example.blescanner.R
import com.example.blescanner.data.ble.BleManager
import com.example.blescanner.data.ble.IBleManager
import com.example.blescanner.data.model.BleDevice
import com.example.blescanner.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import timber.log.Timber

private const val SERVICE_UUID = "ba20fd05-ea08-446d-b007-5dac7b2d1d3b"

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding

    private val devices = ArrayList<BleDevice>()
    private val deviceListAdapter = DeviceListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_main,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        deviceListAdapter.submitList(devices)
        binding.deviceList.apply {
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            adapter = deviceListAdapter
        }

        val bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

        val scanFilters =
            listOf(
                ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString(SERVICE_UUID))
                    .build()
            )

        val scanSettings =
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

        val bleScanner = BleScanner(bluetoothLeScanner, scanFilters, scanSettings)
        val bleManager: IBleManager = BleManager(bleScanner)

        val mainViewModelFactory = MainViewModelFactory(bleManager)
        viewModel = ViewModelProvider(this, mainViewModelFactory)
            .get(MainViewModel::class.java)
        binding.mainViewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {

            binding.fabScan.apply {
                viewModel.isScanning.observe(
                    viewLifecycleOwner,
                    { if (it) setText(R.string.btn_stop_scan) else setText(R.string.btn_start_scan) })
            }

            viewModel.scanFlow.distinctUntilChangedBy { it.address }.collect {
                devices.add(it)
                deviceListAdapter.notifyItemChanged(devices.size - 1)
                Timber.d("onActivityCreated: found device with address = ${it.address}")
            }

        }
    }

}