package com.example.gardenirrigation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends Fragment {

    private static final String[] PERMISSIONS_BLUETOOTH =
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_MOISTURE = "com.example.gardenirrigation.TransferFragment.param.moisture";
    private static final String ARG_PARAM_SSID = "com.example.gardenirrigation.TransferFragment.param.ssid";
    private static final String ARG_PARAM_PASSWORD = "com.example.gardenirrigation.TransferFragment.param.password";

    private String mParamMoisture;
    private String mParamSsid;
    private String mParamPassword;

    private final ActivityResultLauncher<String[]> requestBluetoothPermissionsLauncher =
            registerForActivityResult(
                    new RequestMultiplePermissions(), (isGranted) -> {
                        if (!isGranted.containsValue(false)) {
                            scan(mContext);
                        }
                    }
            );

    public TransferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param moistureLevel The moisture level to be transferred.
     * @param wifiSsid The ssid of the device to be connected to.
     * @param wifiPassword The password of the device to be connected to.
     * @return A new instance of fragment TransferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransferFragment newInstance(String moistureLevel, String wifiSsid, String wifiPassword) {
        TransferFragment fragment = new TransferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_MOISTURE, moistureLevel);
        args.putString(ARG_PARAM_SSID, wifiSsid);
        args.putString(ARG_PARAM_PASSWORD, wifiPassword);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Checks if the app has permission to access Bluetooth. If not, checks if a rationale should be
     * displayed. If so, displays a dialog to the user and then requests the permission. Then, if the
     * user has granted the permission, runs {@link #scan(Context)}.
     *
     * @param context
     */
    private void checkBluetoothPermissionsAndScan(@NonNull Context context) {
        // Ensure all permissions are granted
        boolean areAllPermissionsGranted = true;
        for (String permission : PERMISSIONS_BLUETOOTH) {
            areAllPermissionsGranted &= ContextCompat.checkSelfPermission(context, permission) ==
                                        PackageManager.PERMISSION_GRANTED;
        }

        if (areAllPermissionsGranted) {
            scan(context);
        } else {
            // Check if any rationales are needed
            boolean shouldShowRationale = false;
            for (String permission : PERMISSIONS_BLUETOOTH) {
                shouldShowRationale |= shouldShowRequestPermissionRationale(permission);
            }

            // Show the rationale if needed
            if (shouldShowRationale) {
                showBtPermExplanation();
            } else {
                requestBluetoothPermissionsLauncher.launch(PERMISSIONS_BLUETOOTH);
            }
        }
    }

    private void showBtPermExplanation() {
        // Build an alert dialog to explain why we need Bluetooth permission
        // If the user grants permission, request them from the system
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.bluetooth_permission_explanation)
                .setTitle(R.string.bluetooth_permission_explanation_title)
                .setPositiveButton(R.string.proceed,
                        (dialog, which) -> {
                            requestBluetoothPermissionsLauncher.launch(
                                    PERMISSIONS_BLUETOOTH);
                        })
                .setNegativeButton(R.string.cancel,
                        (dialog, which) -> {
                            dialog.dismiss();
                        })
                .create()
                .show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamMoisture = getArguments().getString(ARG_PARAM_MOISTURE);
            mParamSsid = getArguments().getString(ARG_PARAM_SSID);
            mParamPassword = getArguments().getString(ARG_PARAM_PASSWORD);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }
}