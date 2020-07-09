package com.chip.parkpro1.utils.customViews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chip.parkpro1.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShowLoading extends DialogFragment {

    private String message;
    private ProgressBar progressBar;
    public ShowLoading() {
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(Objects.requireNonNull(getActivity()), getTheme()) {
            @Override
            public void onBackPressed() {
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(false);

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.NoDimDialogFragmentStyle);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        @SuppressLint("InflateParams") View view = localInflater.inflate(R.layout.loading_dialog, null, false);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txt_message = view.findViewById(R.id.txt_message);
        progressBar = view.findViewById(R.id.progressBar);
        txt_message.setText(message);
        progressBar.animate();

        return view;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        progressBar = null;
        super.onDestroyView();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}