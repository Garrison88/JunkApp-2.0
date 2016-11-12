package com.garrisonthomas.junkapp.inputFilters;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

/**
 * Formats the watched EditText to an expiry date
 */
public class CustomTextWatcher implements TextWatcher {

    // Change this to what you want... ' ', '-' etc..
    private char separatingChar;
    private int lengthOfBlock, numberOfBlocks;

    public CustomTextWatcher(int lengthOfBlock, int numberOfBlocks, char separatingChar) {
        this.lengthOfBlock = lengthOfBlock + 1;
        this.numberOfBlocks = numberOfBlocks - 1;
        this.separatingChar = separatingChar;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {
        // Remove spacing char
        if (s.length() > 0 && (s.length() % lengthOfBlock) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (separatingChar == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % lengthOfBlock) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(separatingChar)).length <= numberOfBlocks) {
                s.insert(s.length() - 1, String.valueOf(separatingChar));
            }
        }
    }
}