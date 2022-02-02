package com.example.gardenirrigation;

import static com.welie.blessed.WriteType.WITHOUT_RESPONSE;
import static com.welie.blessed.WriteType.WITH_RESPONSE;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends Fragment {
    private Context mContext;

    UUID serviceUuid = UUID.fromString("91a40d83-3af0-4cb0-a959-97c0d4f74aeb");


    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {
        @Override
        public void onDiscoveredPeripheral(
                @NonNull BluetoothPeripheral peripheral, @NonNull ScanResult scanResult){
            central.stopScan();
            central.connectPeripheral(peripheral, peripheralCallback);
        }
    };
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onCharacteristicUpdate(
                @NonNull BluetoothPeripheral peripheral,
                @NonNull byte[] value,
                @NonNull BluetoothGattCharacteristic characteristic, @NonNull GattStatus status) {
            super.onCharacteristicUpdate(peripheral, value, characteristic, status);
            // Callback for read operations. Results can be accessed here.
        }
        @Override
        public void onCharacteristicWrite(
                @NonNull BluetoothPeripheral peripheral,
                @NonNull byte[] value,
                @NonNull BluetoothGattCharacteristic characteristic, @NonNull GattStatus status) {
            super.onCharacteristicWrite(peripheral, value, characteristic, status);
            // Callback for write operations.
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
            super.onServicesDiscovered(peripheral);
            BluetoothGattService scoutingService = peripheral.getServices().get(0);

            BluetoothGattCharacteristic dataCharacteristic = scoutingService.getCharacteristics().get(0);

            byte[] dataToBeWritten = {0};

            peripheral.writeCharacteristic(dataCharacteristic, dataToBeWritten, WITHOUT_RESPONSE);
        }
    };

    public TransferFragment() {
        // Required empty public constructor
    }

    public static TransferFragment newInstance() {
        return new TransferFragment();
    }

    BluetoothCentralManager central = new BluetoothCentralManager(mContext.getApplicationContext(),
            bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }
}