package com.chip.parkpro1.utils.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.chip.parkpro1.R;

public class RequestStatus extends LinearLayout {

    ImageView imageView;
    ProgressBar progressBar;

    public RequestStatus(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.request_status, this, false);
        progressBar = view.findViewById(R.id.progressBar);
        imageView = view.findViewById(R.id.imageView);
        this.addView(view);
    }

    public void loading() {
        imageView.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
    }

    public void result(Boolean success) {
        progressBar.setVisibility(GONE);
        if (success)
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.checkmark_icon));
        else
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.error_icon));
        imageView.setVisibility(VISIBLE);
    }
}