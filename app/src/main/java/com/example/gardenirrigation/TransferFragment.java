package com.example.gardenirrigation;

import static com.welie.blessed.WriteType.WITHOUT_RESPONSE;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.GattStatus;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends PermissionsFragment implements View.OnClickListener {

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

    // Parameters
    private @NonNull String mParamMoisture;
    private String mParamSsid;
    private String mParamPassword;

    // Members
    private BluetoothCentralManager central;
    private Context mContext;


    // BT Callbacks
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
                    String[] results = new String(value, StandardCharsets.UTF_8).split("!!!");
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

                @Override
                public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
                    super.onServicesDiscovered(peripheral);
                    BluetoothGattService scoutingService = peripheral.getServices().get(0);

                    BluetoothGattCharacteristic dataCharacteristic =
                            scoutingService.getCharacteristics().get(0);

                    byte[] dataToBeWritten =
                            (mParamMoisture + "!!!" + mParamPassword + "!!!" + mParamSsid).getBytes(
                                    StandardCharsets.UTF_8);

                    peripheral.writeCharacteristic(dataCharacteristic, dataToBeWritten,
                            WITHOUT_RESPONSE);
                }
            };
    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback =
            new BluetoothCentralManagerCallback() {
                @Override
                public void onDiscoveredPeripheral(
                        @NonNull BluetoothPeripheral peripheral, @NonNull ScanResult scanResult) {
                    central.stopScan();
                    central.connectPeripheral(peripheral, peripheralCallback);
                }
            };


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TransferFragment.
     */
    public static TransferFragment newInstance() {
        return new TransferFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        central = new BluetoothCentralManager(mContext.getApplicationContext(),
                bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));

        // Get TextView
        TextView textView = view.findViewById(R.id.transfer_text_display);

        // Get parameters
        mParamSsid = TransferFragmentArgs.fromBundle(getArguments()).getSsid();
        mParamPassword = TransferFragmentArgs.fromBundle(getArguments()).getPassword();
        mParamMoisture = TransferFragmentArgs.fromBundle(getArguments()).getMoisture();

        // Set TextView
        String sb = "SSID: " + mParamSsid + "\n" +
                    "PASSWORD: " + mParamPassword + "\n" +
                    "MOISTURE: " + mParamMoisture + "\n";
        textView.setText(sb);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_transfer, container, false);
        Button scanButton = v.findViewById(R.id.transfer_btn_scan);
        scanButton.setOnClickListener(this);
        return v;
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
        central.scanForPeripheralsWithServices(new UUID[]{SERVICE_UUID});
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.transfer_btn_scan) {
            checkPermissionsAndAct(mContext);
        }
    }
}