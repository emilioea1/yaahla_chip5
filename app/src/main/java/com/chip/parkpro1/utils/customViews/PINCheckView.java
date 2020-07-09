package com.chip.parkpro1.utils.customViews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chip.parkpro1.R;

public class PINCheckView extends ConstraintLayout {

    EditText edt_firstCode, edt_secondCode, edt_thirdCode, edt_forthCode;

    public PINCheckView(Context context) {
        super(context);
        init(context);
    }

    public PINCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        View view = inflate(context, R.layout.pin_check_view, this);
        edt_firstCode = view.findViewById(R.id.edt_firstCode);
        edt_secondCode = view.findViewById(R.id.edt_secondCode);
        edt_thirdCode = view.findViewById(R.id.edt_thirdCode);
        edt_forthCode = view.findViewById(R.id.edt_forthCode);

        edt_firstCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edt_firstCode.getText().toString().isEmpty())
                    edt_secondCode.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_secondCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edt_secondCode.getText().toString().isEmpty())
                    edt_thirdCode.requestFocus();
                else {
                    edt_firstCode.requestFocus();
                    if (!edt_firstCode.getText().toString().isEmpty())
                        edt_firstCode.setSelection(1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_thirdCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edt_thirdCode.getText().toString().isEmpty())
                    edt_forthCode.requestFocus();
                else {
                    edt_secondCode.requestFocus();
                    if (!edt_secondCode.getText().toString().isEmpty())
                        edt_secondCode.setSelection(1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_forthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_forthCode.getText().toString().isEmpty())
                    edt_thirdCode.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_secondCode.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                edt_firstCode.requestFocus();
                edt_firstCode.setText("");
                edt_secondCode.setText("");
                return true;
            }
            return false;
        });

        edt_thirdCode.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                edt_secondCode.requestFocus();
                edt_secondCode.setText("");
                edt_thirdCode.setText("");
                return true;
            }
            return false;
        });

        edt_forthCode.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                edt_thirdCode.requestFocus();
                edt_thirdCode.setText("");
                edt_forthCode.setText("");
                return true;
            }
            return false;
        });
    }

    public String getPIN() {
        String pin = edt_firstCode.getText().toString() + edt_secondCode.getText().toString() +
                edt_thirdCode.getText().toString() + edt_forthCode.getText().toString();
        if (pin.length() == 4)
            return pin;
        else
            return null;
    }

    public void clearPIN() {
        edt_firstCode.setText(null);
        edt_secondCode.setText(null);
        edt_thirdCode.setText(null);
        edt_forthCode.setText(null);
    }

    public void canEdit(Boolean canEdit) {
        if (canEdit) {
            edt_firstCode.setEnabled(true);
            edt_secondCode.setEnabled(true);
            edt_thirdCode.setEnabled(true);
            edt_forthCode.setEnabled(true);
        } else {
            edt_firstCode.setEnabled(false);
            edt_secondCode.setEnabled(false);
            edt_thirdCode.setEnabled(false);
            edt_forthCode.setEnabled(false);
        }
    }
}