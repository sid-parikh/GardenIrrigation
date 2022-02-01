package com.example.gardenirrigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.textfield.TextInputLayout;

public class PercentagePreference extends EditTextPreference {
    TextInputLayout mTextInputLayout;

    String mHintText;

    public PercentagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public PercentagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PercentagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PercentagePreference(Context context) {
        super(context);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PercentagePreference);
        mHintText = arr.getString(R.styleable.PercentagePreference_hintText);
        arr.recycle();  // Do this when done.
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mTextInputLayout =
                (TextInputLayout) holder.itemView.findViewById(R.id.numerical_layout_text);
//        EditText editText = mTextInputLayout.getEditText();
//        if (editText != null) {
//            editText.setText(getPersistedInt(25));
//        }
//        mTextInputLayout.setHint(mHintText);
    }




}
