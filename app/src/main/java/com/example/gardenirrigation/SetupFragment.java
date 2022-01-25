package com.example.gardenirrigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup, container, false);
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
        } else if (textInputLayout.getCounterMaxLength() > 0 && input.length() > textInputLayout.getCounterMaxLength()) {
            isError = true;
            textInputLayout.setError(getString(R.string.error_toolong));
        }

        //  If errors were found, add a listener to dismiss the error once it is corrected
        if (isError) {
            editText.addTextChangedListener(new SmallerTextWatcher(textInputLayout, editText) {
                @Override
                public void afterTextChanged(String input, TextInputLayout layout,
                                             EditText editText) {
                    /* Text is not errored if:
                       1. Not Empty
                       2. Digits Only
                       3. Either there is no max or length is <= max
                     */
                    if ((!TextUtils.isEmpty(input) && TextUtils.isDigitsOnly(
                            input)) && (layout.getCounterMaxLength() <= 0 || input.length() <= layout.getCounterMaxLength())) {
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

        public abstract void afterTextChanged(String input, TextInputLayout layout,
                                              EditText editText);
    }
}