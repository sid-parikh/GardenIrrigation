package com.example.gardenirrigation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment {
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    private TextInputLayout mMoistureLevelInputLayout;

    private Context mContext;
    private EditText mSsidEditText;
    private final ActivityResultLauncher<String[]> requestLocationPermissonsLauncher =
            registerForActivityResult(
                    new RequestMultiplePermissions(), (isGranted) -> {
                        if (!isGranted.containsValue(false)) {
                            fillSsid(mContext);
                        }
                    }
            );

    public SetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetupFragment newInstance() {
        return new SetupFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void fillSsid(@NonNull Context context) {
        // Get current WiFi info
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if (ssid != null && !TextUtils.isEmpty(ssid) && !ssid.equalsIgnoreCase("<unknown ssid>")) {
            mSsidEditText.setText(ssid.substring(1, ssid.length() - 1));
        }
    }

    private void checkLocPermsAndFillSsid(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fillSsid(context);
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                   shouldShowRequestPermissionRationale(
                           Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showLocPermExplanation();
        } else {
            requestLocationPermissonsLauncher.launch(PERMISSIONS);
        }
    }

    private void showLocPermExplanation() {
        // Build an alert dialog to explain why we need location permission
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.location_permission_explanation);
        builder.setTitle(R.string.location_permission_explanation_title);
        builder.setPositiveButton(R.string.proceed, (dialog, which) -> {
            requestLocationPermissonsLauncher.launch(PERMISSIONS);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkLocPermsAndFillSsid(mContext);
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
        mSsidEditText = v.findViewById(R.id.setup_edit_ssid);

        // Get text input layout
        mMoistureLevelInputLayout = v.findViewById(R.id.setup_input_moisture);

        return v;
    }

    private void onSubmitButtonClick(View view) {
        if (checkNumberTextInput(mMoistureLevelInputLayout, "Soil Moisture Level is required.")) {
            // Toast with error message
            Toast.makeText(getContext(), "Errors were found.", Toast.LENGTH_SHORT).show();
        } else {
            // Navigate to the transfer fragment
            Navigation.findNavController(view)
                      .navigate(R.id.action_setupFragment_to_transferFragment);
        }

    }

    /**
     * Checks for and displays errors on a given {@link TextInputLayout} and creates a watcher to
     * dismiss the error once it is corrected.
     * <p>
     * The parameters for validation are that the entered input must be 1. Not Empty 2. Digits Only
     * 3. Less than the set max length, if one is set as an attribute of the TextInputLayout.
     *
     * @param textInputLayout      TextInputLayout to monitor
     * @param contextualEmptyError The error message to be displayed if this TextInputLayout is empty.
     * @return whether or not an error was found
     */
    public boolean checkNumberTextInput(final TextInputLayout textInputLayout, String contextualEmptyError) {
        boolean isError = false;
        final EditText editText = textInputLayout.getEditText();

        // This shouldn't happen but TextInputLayout#getEditText() is marked as nullable.
        if (editText == null) {
            return true;
        }
        String input = editText.getText().toString();

        /* Check to make sure the entered text exists, is a number, and is shorter than
           the set max length */
        if (TextUtils.isEmpty(input)) {
            isError = true;
            textInputLayout.setError(contextualEmptyError);
        } else if (!TextUtils.isDigitsOnly(input)) {
            isError = true;
            textInputLayout.setError(getString(R.string.error_notnumber));
        } else if (textInputLayout.getCounterMaxLength() > 0 &&
                   input.length() > textInputLayout.getCounterMaxLength()) {
            isError = true;
            textInputLayout.setError(getString(R.string.error_toolong));
        }

        //  If errors were found, add a listener to dismiss the error once it is corrected
        if (isError) {
            editText.addTextChangedListener(new SmallerTextWatcher(textInputLayout, editText) {
                @Override
                public void afterTextChanged(
                        String input, TextInputLayout layout,
                        EditText editText) {
                    /* Text is not errored if:
                       1. Not Empty
                       2. Digits Only
                       3. Either there is no max or length is <= max
                     */
                    if ((!TextUtils.isEmpty(input) && TextUtils.isDigitsOnly(
                            input)) && (layout.getCounterMaxLength() <= 0 ||
                                        input.length() <= layout.getCounterMaxLength())) {
                        layout.setError(null);
                        editText.removeTextChangedListener(this);
                    }
                }
            });
        }

        return isError;
    }

    /**
     * A smaller version of TextWatcher that automatically implements the two methods that are
     * usually unused. This is a utility to reduce boilerplate code in the main methods.
     */
    private abstract static class SmallerTextWatcher implements TextWatcher {
        private final TextInputLayout mTextInputLayout;
        private final EditText mEditText;

        SmallerTextWatcher(TextInputLayout l, EditText e) {
            mTextInputLayout = l;
            mEditText = e;
        }

        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do nothing
        }

        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing here as it interrupts the user. Prefer afterTextChanged.
        }

        @Override
        public final void afterTextChanged(Editable s) {
            // Defer to a more useful method
            afterTextChanged(s.toString(), mTextInputLayout, mEditText);
        }

        public abstract void afterTextChanged(
                String input, TextInputLayout layout,
                EditText editText);
    }
}