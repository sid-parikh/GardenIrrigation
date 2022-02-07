package com.example.gardenirrigation;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends PermissionsFragment {

    /**
     * The TextInputLayout for the Moisture Level
     */
    private TextInputLayout mMoistureLevelInputLayout;
    /**
     * Saves context for later use
     */
    private Context mContext;


    /**
     * The TextInputLayout for the WiFi SSID
     */
    private TextInputLayout mSsidInputLayout;
    private TextInputLayout mPasswordInputLayout;

    public SetupFragment() {
        // Required empty public constructor
    }


    /**
     * Returns a new instance of this fragment.
     *
     * @return A new instance of fragment SetupFragment.
     */
    public static SetupFragment newInstance() {
        return new SetupFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Save context for later use
        mContext = context;
    }


    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPermissionsAndAct(mContext);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setup, container, false);

        // Get submit button
        Button mSubmitButton = v.findViewById(R.id.setup_button_submit);
        mSubmitButton.setOnClickListener(this::onSubmitButtonClick);

        // Get SSID text input layout
        mSsidInputLayout = v.findViewById(R.id.setup_input_ssid);
        // Get Password text input layout
        mPasswordInputLayout = v.findViewById(R.id.setup_input_password);

        // Get moisture text input layout
        mMoistureLevelInputLayout = v.findViewById(R.id.setup_input_moisture);

        return v;
    }

    /**
     * When the submit button is clicked, checks for valid input and then sends the data to  {@link TransferFragment}.
     *
     * @param view The view that was clicked.
     */
    private void onSubmitButtonClick(View view) {
        if (TextInputUtils.validateNumberTextInput(mMoistureLevelInputLayout,
                getString(R.string.error_moisture_required),
                getString(R.string.error_moisture_notnumber),
                getString(R.string.error_moisture_toolong))
            & TextInputUtils.validateStringTextInput(mSsidInputLayout,
                getString(R.string.error_ssid_required),
                getString(R.string.error_ssid_toolong))
            & TextInputUtils.validateStringTextInput(mPasswordInputLayout,
                getString(R.string.error_password_missing),
                getString(R.string.error_password_toolong))) {

            // Get the SSID and password
            String ssid =
                    Objects.requireNonNull(mSsidInputLayout.getEditText(), "No SSID Edit Text!")
                           .getText()
                           .toString();
            String password = Objects.requireNonNull(mPasswordInputLayout.getEditText(),
                    "No Password Edit Text!").getText().toString();
            // Get the moisture level
            int moistureLevel =
                    Integer.parseInt(Objects.requireNonNull(mMoistureLevelInputLayout.getEditText(),
                            "No Moisture Level Edit Text!").getText().toString());

            // Send the data to the transfer fragment
            SetupFragmentDirections.ActionSetupFragmentToTransferFragment action =
                    SetupFragmentDirections.actionSetupFragmentToTransferFragment();
            action.setSsid(ssid);
            action.setPassword(password);
            action.setMoisture(moistureLevel);

            // Navigate to the transfer fragment
            Navigation.findNavController(view).navigate(action);
        } else {
            // Toast with error message
            Toast.makeText(getContext(), "Errors were found.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected String[] getPermissionsToRequest() {
        // Notify superclass that we need Fine Location Permission.
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }

    /**
     * Fills the SSID EditText with the SSID of the current WiFi connection.
     * Note: This requires the ACCESS_FINE_LOCATION permission.
     */
    @Override
    protected void onPermissionsGranted() {
        // Get current WiFi info
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // Get SSID
        String ssid = wifiInfo.getSSID();
        if (ssid != null && !TextUtils.isEmpty(ssid) && !ssid.equalsIgnoreCase("<unknown ssid>")) {
            Objects.requireNonNull(mSsidInputLayout.getEditText())
                   .setText(ssid.substring(1, ssid.length() - 1));
        }
    }


}