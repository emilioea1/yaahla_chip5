package com.chip.parkpro1.ui.base;

public interface BaseActivityCallback {

    void showProgressDialog(String message);

    void setToolbarTitle(String title);

    void setToolbarIconVisibility(int Visibility);

    void hideProgressDialog();

    void logout();
}
