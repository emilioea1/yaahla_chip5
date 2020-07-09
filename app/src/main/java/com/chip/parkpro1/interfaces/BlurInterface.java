package com.chip.parkpro1.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface BlurInterface {
    void blurBackground(int visibility, int cornerRadius);

    void hideRecycler(RecyclerView recyclerView);

    void getSelectedCarId(String carId);
}
