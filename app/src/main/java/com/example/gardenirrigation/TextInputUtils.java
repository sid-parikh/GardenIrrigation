package com.example.gardenirrigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class TextInputUtils {

    public static boolean validateStringTextInput(final TextInputLayout textInputLayout, String emptyError, String tooLongError) {
        boolean isError = false;
        final EditText editText = textInputLayout.getEditText();

        // This shouldn't happen but TextInputLayout#getEditText() is marked as nullable.
        if (editText == null) {
            return true;
        }
        String input = editText.getText().toString();

        /* Check to make sure the input is not empty, and is shorter than the max length. */
        if (TextUtils.isEmpty(input)) {
            isError = true;
            textInputLayout.setError(emptyError);
        } else if (textInputLayout.getCounterMaxLength() > 0 &&
                   input.length() > textInputLayout.getCounterMaxLength()) {
            isError = true;
            textInputLayout.setError(tooLongError);
        }

        // If errors are found, add a listener to dismiss the error once it is corrected.
        if (isError) {
            editText.addTextChangedListener(new SmallerTextWatcher(textInputLayout) {
                @Override
                public void afterTextChanged(String input, TextInputLayout layout, EditText editText) {
                    /* Text has no errors if:
                       1. Not Empty
                       2. Either there is no max or length is <= max
                     */
                    if ((!TextUtils.isEmpty(input)) && (layout.getCounterMaxLength() <= 0 ||
                                                       input.length() <=
                                                       layout.getCounterMaxLength())) {
                        layout.setError(null);
                        editText.removeTextChangedListener(this);
                    }
                }
            });
        }

        return !isError;

    }

    /**
     * Checks for and displays errors on a given number-input {@link TextInputLayout} and creates a
     * {@link SmallerTextWatcher} to dismiss the error once it is corrected.
     * <p>
     * The parameters for validation are that the entered input must be 1. Not Empty 2. Digits Only
     * 3. Less than the set max length, if one is set as an attribute of the TextInputLayout.
     *
     * @param textInputLayout      TextInputLayout to monitor
     * @param emptyError The error message to be displayed if this TextInputLayout is empty.
     * @return whether or not an error was found
     */
    public static boolean validateNumberTextInput(final TextInputLayout textInputLayout, String emptyError, String notNumberError, String tooLongError) {
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
            textInputLayout.setError(emptyError);
        } else if (!TextUtils.isDigitsOnly(input)) {
            isError = true;
            textInputLayout.setError(notNumberError);
        } else if (textInputLayout.getCounterMaxLength() > 0 &&
                   input.length() > textInputLayout.getCounterMaxLength()) {
            isError = true;
            textInputLayout.setError(tooLongError);
        }

        //  If errors were found, add a listener to dismiss the error once it is corrected
        if (isError) {
            editText.addTextChangedListener(new SmallerTextWatcher(textInputLayout) {
                @Override
                public void afterTextChanged(
                        String input, TextInputLayout layout,
                        EditText editText) {
                    /* Text has no errors if:
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

        return !isError;
    }


    /**
     * A smaller version of TextWatcher that automatically implements the two methods that are
     * usually unused. It also replaces the third method with a more useful one, providing the
     * subclass with more information. This is a utility to reduce boilerplate code in the main methods.
     */
    public abstract static class SmallerTextWatcher implements TextWatcher {
        private final TextInputLayout mTextInputLayout;
        private final EditText mEditText;

        SmallerTextWatcher(TextInputLayout l) {
            mTextInputLayout = l;
            mEditText = l.getEditText();
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
