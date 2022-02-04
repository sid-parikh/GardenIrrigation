package com.example.gardenirrigation;

import static com.welie.blessed.WriteType.WITHOUT_RESPONSE;
import static com.welie.blessed.WriteType.WITH_RESPONSE;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.UUID;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.GattStatus;

import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends PermissionsFragment {

    // Argument keys
    private static final String ARG_PARAM_MOISTURE =
            "com.example.gardenirrigation.TransferFragment.param.moisture";
    private static final String ARG_PARAM_SSID =
            "com.example.gardenirrigation.TransferFragment.param.ssid";
    private static final String ARG_PARAM_PASSWORD =
            "com.example.gardenirrigation.TransferFragment.param.password";


    // UUID for bluetooth communications
    private static final UUID SERVICE_UUID =
            UUID.fromString("91a40d83-3af0-4cb0-a959-97c0d4f74aeb");
    // Define callbacks for when we connect to the bluetooth device
    private final BluetoothPeripheralCallback peripheralCallback =
            new BluetoothPeripheralCallback() {
                @Override
                public void onCharacteristicUpdate(
                        @NonNull BluetoothPeripheral peripheral,
                        @NonNull byte[] value,
                        @NonNull BluetoothGattCharacteristic characteristic,
                        @NonNull GattStatus status) {
                    super.onCharacteristicUpdate(peripheral, value, characteristic, status);
                    // Callback for read operations. Results can be accessed here.
                }

                @Override
                public void onCharacteristicWrite(
                        @NonNull BluetoothPeripheral peripheral,
                        @NonNull byte[] value,
                        @NonNull BluetoothGattCharacteristic characteristic,
                        @NonNull GattStatus status) {
                    super.onCharacteristicWrite(peripheral, value, characteristic, status);
                    // Callback for write operations.
                }
    private String mParamMoisture;
    private String mParamSsid;
    private String mParamPassword;

    BluetoothCentralManager central;

    private final ActivityResultLauncher<String[]> requestBluetoothPermissionsLauncher =
            registerForActivityResult(
                    new RequestMultiplePermissions(), (isGranted) -> {
                        if (!isGranted.containsValue(false)) {
                            central.scanForPeripheralsWithServices(new UUID[] {serviceUuid});
                        }
                    }
            );
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
            String[] results = value.toString().split("!!!");
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

            byte[] dataToBeWritten = (mParamMoisture + "!!!" + mParamPassword + "!!!" + mParamSsid).getBytes();

            peripheral.writeCharacteristic(dataCharacteristic, dataToBeWritten, WITHOUT_RESPONSE);
        }
    };

    public TransferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param moistureLevel The moisture level to be transferred.
     * @param wifiSsid      The ssid of the device to be connected to.
     * @param wifiPassword  The password of the device to be connected to.
     * @return A new instance of fragment TransferFragment.
     */
    public static TransferFragment newInstance(int moistureLevel, String wifiSsid, String wifiPassword) {
        TransferFragment fragment = new TransferFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_MOISTURE, moistureLevel);
        args.putString(ARG_PARAM_SSID, wifiSsid);
        args.putString(ARG_PARAM_PASSWORD, wifiPassword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamMoisture = getArguments().getInt(ARG_PARAM_MOISTURE);
            mParamSsid = getArguments().getString(ARG_PARAM_SSID);
            mParamPassword = getArguments().getString(ARG_PARAM_PASSWORD);
        }
        central = new BluetoothCentralManager(mContext.getApplicationContext(),
                bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }


    @Override
    protected String[] getPermissionsToRequest() {
        // Permissions to ask for, dependent on version
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
               new String[]{
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.BLUETOOTH_SCAN,
                       Manifest.permission.BLUETOOTH_CONNECT,
                       Manifest.permission.BLUETOOTH_ADVERTISE
               } : new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                };
    }

    @Override
    protected void onPermissionsGranted() {
        // scan(mContext);
    }
}